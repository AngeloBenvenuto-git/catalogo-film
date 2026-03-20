import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FilmService } from '../../services/film';
import { AuthService } from '../../services/auth.service';
import { RecensioneService } from '../../services/recensione.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-film-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './film-detail.html',
  styleUrl: './film-detail.css'
})
export class FilmDetail implements OnInit {
  film: any;
  trailerUrl: SafeResourceUrl | null = null;
  modalitaModifica: boolean = false;
  filmModifica: any = {};
  recensioni: any[] = [];
  nuovaRecensione: string = '';
  nuovoVoto: number = 5;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private filmService: FilmService,
    private authService: AuthService,
    private recensioneService: RecensioneService,
    private cdr: ChangeDetectorRef,
    private location: Location,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.filmService.getFilmById(id).subscribe({
      next: (res) => {
        this.film = res;
        this.filmModifica = { ...res };
        if (this.film.tmdbId) {
          this.caricaTrailer(this.film.tmdbId);
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error("Errore nel recupero del film", err)
    });
    this.caricaRecensioni(id);
  }

  caricaRecensioni(filmId: number) {
    this.recensioneService.getRecensioniFilm(filmId).subscribe({
      next: (data) => {
        this.recensioni = [...data];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Errore caricamento recensioni', err)
    });
  }

  inviaRecensione() {
    if (!this.nuovaRecensione.trim()) return;
    this.recensioneService.aggiungiRecensione(this.film.id, this.nuovaRecensione, this.nuovoVoto).subscribe({
      next: () => {
        this.nuovaRecensione = '';
        this.nuovoVoto = 5;
        this.cdr.detectChanges();
        this.caricaRecensioni(this.film.id);
      },
      error: (err) => console.error('Errore invio recensione', err)
    });
  }

  cancellaRecensione(id: number) {
    if (confirm('Sei sicuro di voler eliminare questa recensione?')) {
      this.recensioneService.cancellaRecensione(id).subscribe({
        next: () => this.caricaRecensioni(this.film.id)
      });
    }
  }

  calcolaMediaVoti(): number {
    if (this.recensioni.length === 0) return 0;
    const somma = this.recensioni.reduce((acc, r) => acc + r.voto, 0);
    return somma / this.recensioni.length;
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isAdminOrRedattore(): boolean {
    const ruolo = this.authService.getRuolo();
    return ruolo === 'ADMIN' || ruolo === 'REDATTORE';
  }

  isAdmin(): boolean {
    return this.authService.getRuolo() === 'ADMIN';
  }

  eliminaFilm() {
    if (confirm('Sei sicuro di voler eliminare questo film?')) {
      this.filmService.eliminaFilm(this.film.id).subscribe({
        next: () => {
          alert('Film eliminato con successo!');
          this.router.navigate(['/']);
        },
        error: (err) => console.error('Errore eliminazione film', err)
      });
    }
  }

  salvaModifica() {
    const filmDaModificare = {
      titolo: this.filmModifica.titolo,
      trama: this.filmModifica.trama,
      anno: this.filmModifica.anno,
      durata: this.filmModifica.durata,
      valutazione: this.filmModifica.valutazione,
      tipologia: this.filmModifica.tipologia,
      posterUrl: this.filmModifica.posterUrl,
      tmdbId: this.filmModifica.tmdbId
    };

    this.filmService.modificaFilm(this.film.id, filmDaModificare).subscribe({
      next: (res) => {
        this.film = res;
        this.modalitaModifica = false;
        this.cdr.detectChanges();
        alert('Film modificato con successo!');
      },
      error: (err) => console.error('Errore modifica film', err)
    });
  }

  caricaTrailer(tmdbId: number) {
    const apiKey = '9579a0d27a808bfbf8073387eefbddad';
    const url = `https://api.themoviedb.org/3/movie/${tmdbId}/videos?api_key=${apiKey}&language=it-IT`;

    fetch(url)
      .then(response => response.json())
      .then(data => {
        let video = data.results?.find((v: any) => v.type === 'Trailer' && v.site === 'YouTube');
        if (!video) {
          return fetch(`https://api.themoviedb.org/3/movie/${tmdbId}/videos?api_key=${apiKey}&language=en-US`)
            .then(res => res.json());
        }
        return { results: [video] };
      })
      .then(data => {
        const video = data.results?.find((v: any) => v.type === 'Trailer' || v.type === 'Teaser');
        if (video && video.key) {
          this.trailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${video.key}`);
        } else {
          const queryRicerca = encodeURIComponent(`${this.film.titolo} trailer ufficiale`);
          this.trailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
            `https://www.youtube.com/embed?listType=search&list=${queryRicerca}`
          );
        }
        this.cdr.detectChanges();
      })
      .catch(err => {
        const queryRicerca = encodeURIComponent(`${this.film.titolo} trailer`);
        this.trailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
          `https://www.youtube.com/embed?listType=search&list=${queryRicerca}`
        );
        this.cdr.detectChanges();
      });
  }

  tornaIndietro() {
    this.location.back();
  }

  haGiaRecensito(): boolean {
    const username = this.authService.getUsername();
    return this.recensioni.some(r => r.usernameUtente === username);
  }

  isRecensioneUtente(usernameUtente: string): boolean {
    return this.authService.getUsername() === usernameUtente;
  }
}
