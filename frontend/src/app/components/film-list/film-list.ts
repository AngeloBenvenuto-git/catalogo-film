import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FilmService } from '../../services/film';

@Component({
  selector: 'app-film-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './film-list.html',
  styleUrl: './film-list.css',
})
export class FilmList implements OnInit {
  films: any[] = [];
  filmsFiltrati: any[] = [];
  ricerca: string = '';
  ordinamento: string = '';
  valutazioneMin: number = 0;
  valutazioneMax: number = 100;
  mostraFiltroValutazione: boolean = false;
  mostraFiltroAnno: boolean = false;

  intervalliAnni = [
    { label: '- 1980', min: 0, max: 1980 },
    { label: '1980 - 1990', min: 1980, max: 1990 },
    { label: '1990 - 2000', min: 1990, max: 2000 },
    { label: '2000 - 2010', min: 2000, max: 2010 },
    { label: '2010 - 2020', min: 2010, max: 2020 },
    { label: '2020+', min: 2020, max: 9999 },
  ];

  intervalloAperto: string | null = null;
  anniSelezionati: Set<number> = new Set();
  ordinamentoAnno: string = '';

  constructor(
    private filmService: FilmService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.filmService.getTuttiFilm().subscribe({
      next: (data) => {
        this.films = data;
        this.filmsFiltrati = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Errore nel caricamento film:', err),
    });
  }

  getAnniPerIntervallo(min: number, max: number): number[] {
    const anni: number[] = [];
    const annoMax = max === 9999 ? new Date().getFullYear() : max - 1;
    for (let a = min; a <= annoMax; a++) {
      if (this.films.some(f => f.anno === a)) anni.push(a);
    }
    return anni;
  }

  toggleAnno(anno: number) {
    if (this.anniSelezionati.has(anno)) {
      this.anniSelezionati.delete(anno);
    } else {
      this.anniSelezionati.add(anno);
    }
    this.applicaOrdinamento();
  }

  cercaFilm() {
    this.applicaOrdinamento();
  }

  applicaOrdinamento() {
    let risultato = this.films.filter(f => {
      const titoloOk = f.titolo.toLowerCase().includes(this.ricerca.toLowerCase());
      const valOk = f.valutazione * 10 >= this.valutazioneMin && f.valutazione * 10 <= this.valutazioneMax;
      const annoOk = this.anniSelezionati.size === 0 || this.anniSelezionati.has(f.anno);
      return titoloOk && valOk && annoOk;
    });

    if (this.ordinamento === 'val-desc') risultato.sort((a, b) => b.valutazione - a.valutazione);
    if (this.ordinamento === 'val-asc') risultato.sort((a, b) => a.valutazione - b.valutazione);
    if (this.ordinamentoAnno === 'anno-desc') risultato.sort((a, b) => b.anno - a.anno);
    if (this.ordinamentoAnno === 'anno-asc') risultato.sort((a, b) => a.anno - b.anno);

    this.filmsFiltrati = risultato;
  }

  ordinaPerAnno() {
    this.ordinamentoAnno = this.ordinamentoAnno === 'anno-asc' ? 'anno-desc' : 'anno-asc';
    this.applicaOrdinamento();
  }

    rimuoviFiltri() {
    this.ricerca = '';
    this.ordinamento = '';
    this.ordinamentoAnno = '';
    this.valutazioneMin = 0;
    this.valutazioneMax = 100;
    this.anniSelezionati = new Set();
    this.mostraFiltroValutazione = false;
    this.mostraFiltroAnno = false;
    this.intervalloAperto = null;
    this.filmsFiltrati = this.films;
  }
  
  vaiAiDettagli(id: number) {
    this.router.navigate(['/film-detail', id]);
  }
}