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

  cercaFilm() {
    if (this.ricerca.trim() === '') {
      this.filmsFiltrati = this.films;
    } else {
      this.filmsFiltrati = this.films.filter(f =>
        f.titolo.toLowerCase().includes(this.ricerca.toLowerCase())
      );
    }
  }

  ordinaPerValutazione() {
    this.filmsFiltrati = [...this.filmsFiltrati].sort((a, b) => b.valutazione - a.valutazione);
  }

  ordinaPerAnno() {
    this.filmsFiltrati = [...this.filmsFiltrati].sort((a, b) => b.anno - a.anno);
  }

  vaiAiDettagli(id: number) {
    this.router.navigate(['/film-detail', id]);
  }
}