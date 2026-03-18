import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FilmService } from '../../services/film';

@Component({
  selector: 'app-film-list',
  standalone: true,
  imports: [CommonModule, FormsModule], // RouterLink rimosso qui per togliere l'errore
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
    this.valutazioneMin = Number(localStorage.getItem('f_val_min')) || 0;
    this.valutazioneMax = Number(localStorage.getItem('f_val_max')) || 10;
    const anniSalvati = localStorage.getItem('f_anni');
    if (anniSalvati) this.anniSelezionati = new Set(JSON.parse(anniSalvati));

    this.filmService.getTuttiFilm().subscribe({
      next: (data) => {
        this.films = data;
        this.applicaOrdinamento();
        this.cdr.detectChanges();
      }
    });
  }

  vaiAiDettagli(id: number) {
    this.salvaStatoBrowser();
    this.router.navigate(['/films', id]);
  }

  private salvaStatoBrowser() {
    localStorage.setItem('f_ricerca', this.ricerca);
    localStorage.setItem('f_val_min', this.valutazioneMin.toString());
    localStorage.setItem('f_val_max', this.valutazioneMax.toString());
    localStorage.setItem('f_anni', JSON.stringify(Array.from(this.anniSelezionati)));
  }

  applicaOrdinamento() {
    this.filmsFiltrati = this.films.filter(f => {
      const titoloOk = f.titolo.toLowerCase().includes(this.ricerca.toLowerCase());
      const valOk = f.valutazione >= this.valutazioneMin && f.valutazione <= this.valutazioneMax;
      const annoOk = this.anniSelezionati.size === 0 || this.anniSelezionati.has(f.anno);
      return titoloOk && valOk && annoOk;
    });
    this.salvaStatoBrowser();
  }

  // Aggiungi qui le altre funzioni (toggleAnno, cercaFilm, rimuoviFiltri, getAnniPerIntervallo)
}
