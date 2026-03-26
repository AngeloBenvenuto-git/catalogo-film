import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ListaCurataService } from '../../services/lista-curata.service';
import { AuthService } from '../../services/auth.service';
import { FilmService } from '../../services/film';

@Component({
  selector: 'app-lista-curata',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './lista-curata.component.html',
  styleUrls: ['./lista-curata.component.css']
})
export class ListaCurataComponent implements OnInit {
  liste: any[] = [];
  listaInModifica: any = null;
  queryRicerca: string = '';
  risultatiRicerca: any[] = [];

  constructor(
    private listaService: ListaCurataService,
    private filmService: FilmService,
    public authService: AuthService,
    private router: Router,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.caricaListe();
  }

  caricaListe(): void {
    this.listaService.getTutteLeListe().subscribe({
      next: (res) => {
        this.liste = res;

        // SINCRONIZZAZIONE: Se l'utente sta modificando una lista,
        // aggiorniamo l'oggetto locale con i nuovi dati (e i nuovi ID film) dal server
        if (this.listaInModifica) {
          const aggiornata = this.liste.find(l => l.id === this.listaInModifica.id);
          if (aggiornata) {
            // Usiamo una copia pulita per evitare conflitti di memoria
            this.listaInModifica = JSON.parse(JSON.stringify(aggiornata));
          }
        }
        this.cd.detectChanges();
      },
      error: (err) => console.error("Errore caricamento liste:", err)
    });
  }

  selezionaListaPerModifica(lista: any): void {
    this.listaInModifica = JSON.parse(JSON.stringify(lista));
    this.queryRicerca = '';
    this.risultatiRicerca = [];
    setTimeout(() => {
      document.getElementById('editPanel')?.scrollIntoView({ behavior: 'smooth' });
    }, 100);
  }

  puoGestire(usernameRedattore: string): boolean {
    const userLoggato = this.authService.getUsername();
    const ruolo = this.authService.getRuolo();
    return ruolo === 'ADMIN' || ruolo === 'ROLE_ADMIN' || userLoggato === usernameRedattore;
  }

  salvaModificheDatiGenerali(): void {
    if (!this.listaInModifica) return;
    const payload = {
      titolo: this.listaInModifica.titolo,
      descrizione: this.listaInModifica.descrizione
    };
    this.listaService.updateLista(this.listaInModifica.id, payload).subscribe({
      next: () => {
        alert("Informazioni aggiornate!");
        this.caricaListe();
      },
      error: (err) => alert("Errore nel salvataggio dei dati generali.")
    });
  }

  cercaFilm(): void {
    if (this.queryRicerca.trim().length > 2) {
      this.filmService.cercaFilm(this.queryRicerca).subscribe({
        next: (res: any[]) => {
          this.risultatiRicerca = res.slice(0, 5);
          this.cd.detectChanges();
        }
      });
    } else {
      this.risultatiRicerca = [];
    }
  }

  aggiungiFilm(filmId: number): void {
    if (!this.listaInModifica) return;
    this.listaService.aggiungiFilmALista(this.listaInModifica.id, filmId).subscribe({
      next: () => {
        this.queryRicerca = '';
        this.risultatiRicerca = [];
        this.filmService.resetCache(); // Fondamentale per la Home
        this.caricaListe(); // Aggiorna tutto istantaneamente
      },
      error: (err) => console.error("Errore aggiunta film", err)
    });
  }

  rimuoviFilm(filmId: number): void {
    if (!this.listaInModifica || !filmId) return;

    if (confirm("Rimuovere questo film dalla collezione?")) {
      this.listaService.rimuoviFilmDaLista(this.listaInModifica.id, filmId).subscribe({
        next: () => {
          this.filmService.resetCache();
          this.caricaListe(); // Sincronizza il pannello e la card
        },
        error: (err) => console.error("Errore rimozione film", err)
      });
    }
  }

  onLike(id: number): void {
    this.listaService.toggleLike(id).subscribe({
      next: () => this.caricaListe()
    });
  }

  eliminaLista(id: number): void {
    if (confirm("Sei sicuro di voler eliminare definitivamente questa lista?")) {
      this.listaService.cancellaLista(id).subscribe({
        next: () => {
          this.listaInModifica = null;
          this.caricaListe();
        }
      });
    }
  }

  vaiACreaLista(): void {
    this.router.navigate(['/crea-lista']);
  }
}
