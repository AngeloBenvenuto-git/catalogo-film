import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FilmService } from '../../services/film';

@Component({
  selector: 'app-film-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './film-list.html',
  styleUrl: './film-list.css',
})
export class FilmList implements OnInit {
  films: any[] = [];          // Database originale (non viene mai toccato)
  filmsFiltrati: any[] = [];  // Lista mostrata nell'HTML

  // Parametri URL
  ricerca: string = '';
  genereSelezionato: string = '';
  ordinamento: string = '';
  ordinamentoAnno: string = '';

  // Filtri tecnici (opzionali)
  valutazioneMin: number = 0;
  valutazioneMax: number = 10;
  anniSelezionati: Set<number> = new Set();

  constructor(
    private filmService: FilmService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    // 1. Carichiamo i film dal database
    this.filmService.getTuttiFilm().subscribe({
      next: (data) => {
        this.films = data;

        // 2. Ascoltiamo l'URL. Ogni volta che cambia (click sulla Navbar), rieseguiamo la logica.
        this.route.queryParams.subscribe(params => {
          this.ricerca = params['q'] || '';
          this.genereSelezionato = params['g'] || '';
          this.ordinamento = params['ord'] || '';
          this.ordinamentoAnno = params['ordAnno'] || '';

          this.applicaLogicaProfessionale();
          this.cdr.detectChanges();
        });
      }
    });
  }

  applicaLogicaProfessionale() {
    console.log('Genere selezionato:', this.genereSelezionato);
    if (this.films.length > 0) {
      console.log('Generi primo film:', this.films[0].generi);
    }
    // Partiamo da una copia pulita dell'intero database
    let risultato = [...this.films];

    // --- A. FILTRAGGIO ---

    // 1. Filtro per Genere (LA PARTE CORRETTA)
    if (this.genereSelezionato) {
      const gCercato = this.genereSelezionato.toLowerCase().trim();

      risultato = risultato.filter(f => {
        // Caso 1: Se il backend manda 'generi' come Array (es. ["Azione", "Horror"] o [{nome: "Azione"}])
        if (f.generi && Array.isArray(f.generi)) {
          return f.generi.some((g: any) => {
            // Controlla sia se 'g' è una semplice stringa, sia se è un oggetto con dentro 'nome'
            const nomeGenere = typeof g === 'string' ? g : (g.nome || '');
            return nomeGenere.toLowerCase().includes(gCercato);
          });
        }

        // Caso 2: Fallback se il DTO manda tutto in una singola stringa 'genere' o usa 'tipologia'
        const campoSingolo = (f.genere || f.tipologia || '').toString().toLowerCase();
        return campoSingolo.includes(gCercato);
      });
    }

    // 2. Filtro per Ricerca Testuale (se presente)
    if (this.ricerca) {
      const qCercata = this.ricerca.toLowerCase().trim();
      risultato = risultato.filter(f =>
        f.titolo.toLowerCase().includes(qCercata)
      );
    }

    // 3. Filtri tecnici (Valutazione/Range anni se attivi)
    risultato = risultato.filter(f => {
      const valOk = f.valutazione >= this.valutazioneMin && f.valutazione <= this.valutazioneMax;
      const annoOk = this.anniSelezionati.size === 0 || this.anniSelezionati.has(f.anno);
      return valOk && annoOk;
    });

    // --- B. ORDINAMENTO ---

    // Ordinamento Voto
    if (this.ordinamento === 'val-desc') {
      risultato.sort((a, b) => b.valutazione - a.valutazione);
    } else if (this.ordinamento === 'val-asc') {
      risultato.sort((a, b) => a.valutazione - b.valutazione);
    }

    // Ordinamento Anno
    if (this.ordinamentoAnno === 'anno-desc') {
      risultato.sort((a, b) => b.anno - a.anno);
    } else if (this.ordinamentoAnno === 'anno-asc') {
      risultato.sort((a, b) => a.anno - b.anno);
    }

    // --- C. AGGIORNAMENTO VISTA ---
    this.filmsFiltrati = risultato;
  }

  /**
   * Reset totale: Pulizia URL e ripristino stato originale
   */
  rimuoviFiltri() {
    localStorage.clear();
    this.valutazioneMin = 0;
    this.valutazioneMax = 10;
    this.anniSelezionati = new Set();

    // Naviga all'URL pulito
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {},
      queryParamsHandling: '' // Rimuove tutto
    });
  }

  vaiAiDettagli(id: number) {
    this.router.navigate(['/films', id]);
  }
}
