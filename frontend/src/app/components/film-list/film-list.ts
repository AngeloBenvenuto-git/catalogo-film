import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
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
  films: any[] = [];
  filmsFiltrati: any[] = [];
  ricerca: string = '';
  ordinamento: string = '';
  valutazioneMin: number = 0;
  valutazioneMax: number = 10;
  ordinamentoAnno: string = '';
  anniSelezionati: Set<number> = new Set();
  mostraFiltroValutazione: boolean = false;
  mostraFiltroAnno: boolean = false;
  intervalloAperto: string | null = null;

  intervalliAnni = [
    { label: '- 1980', min: 0, max: 1980 },
    { label: '1980 - 1990', min: 1980, max: 1990 },
    { label: '1990 - 2000', min: 1990, max: 2000 },
    { label: '2000 - 2010', min: 2000, max: 2010 },
    { label: '2010 - 2020', min: 2010, max: 2020 },
    { label: '2020+', min: 2020, max: 9999 },
  ];

  constructor(private filmService: FilmService, private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.ricerca = localStorage.getItem('f_ricerca') || '';
    this.ordinamento = localStorage.getItem('f_ord_val') || '';
    this.ordinamentoAnno = localStorage.getItem('f_ord_anno') || '';
    this.valutazioneMin = Number(localStorage.getItem('f_val_min')) || 0;
    this.valutazioneMax = Number(localStorage.getItem('f_val_max')) || 10;

    const anniSalvati = localStorage.getItem('f_anni');
    if (anniSalvati) {
      this.anniSelezionati = new Set(JSON.parse(anniSalvati));
    }

    this.filmService.getTuttiFilm().subscribe({
      next: (data) => {
        this.films = data;
        this.applicaOrdinamento();
        this.cdr.detectChanges();
      }
    });
  }

  // --- GESTIONE NAVIGAZIONE ---
  vaiAiDettagli(id: number) {
    this.salvaStatoBrowser();
    this.router.navigate(['/films', id]);
  }

  // --- LOGICA FILTRI ---
  cercaFilm() {
    this.applicaOrdinamento();
  }

  toggleAnno(anno: number) {
    if (this.anniSelezionati.has(anno)) {
      this.anniSelezionati.delete(anno);
    } else {
      this.anniSelezionati.add(anno);
    }
    this.applicaOrdinamento();
    this.cdr.detectChanges();
  }

  getAnniPerIntervallo(min: number, max: number): number[] {
    const anni: number[] = [];
    const annoMax = max === 9999 ? new Date().getFullYear() : max;
    for (let a = min; a <= annoMax; a++) {
      if (this.films.some(f => f.anno === a)) {
        anni.push(a);
      }
    }
    return anni;
  }

  applicaOrdinamento() {
    let risultato = this.films.filter(f => {
      const titoloOk = f.titolo.toLowerCase().includes(this.ricerca.toLowerCase());
      const valOk = f.valutazione >= this.valutazioneMin && f.valutazione <= this.valutazioneMax;
      const annoOk = this.anniSelezionati.size === 0 || this.anniSelezionati.has(f.anno);
      return titoloOk && valOk && annoOk;
    });

    // Applica Ordinamenti
    if (this.ordinamento === 'val-desc') risultato.sort((a, b) => b.valutazione - a.valutazione);
    if (this.ordinamento === 'val-asc') risultato.sort((a, b) => a.valutazione - b.valutazione);
    if (this.ordinamentoAnno === 'anno-desc') risultato.sort((a, b) => b.anno - a.anno);
    if (this.ordinamentoAnno === 'anno-asc') risultato.sort((a, b) => a.anno - b.anno);

    this.filmsFiltrati = risultato;
    this.salvaStatoBrowser();
  }

  rimuoviFiltri() {
    localStorage.clear();
    this.ricerca = '';
    this.ordinamento = '';
    this.ordinamentoAnno = '';
    this.valutazioneMin = 0;
    this.valutazioneMax = 10;
    this.anniSelezionati = new Set();
    this.applicaOrdinamento();
    this.cdr.detectChanges();
  }

  private salvaStatoBrowser() {
    localStorage.setItem('f_ricerca', this.ricerca);
    localStorage.setItem('f_val_min', this.valutazioneMin.toString());
    localStorage.setItem('f_val_max', this.valutazioneMax.toString());
    localStorage.setItem('f_anni', JSON.stringify(Array.from(this.anniSelezionati)));
    localStorage.setItem('f_ord_val', this.ordinamento);
    localStorage.setItem('f_ord_anno', this.ordinamentoAnno);
  }
}
