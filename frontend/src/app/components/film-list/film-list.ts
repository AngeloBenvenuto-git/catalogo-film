import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FilmService } from '../../services/film';

@Component({
  selector: 'app-film-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './film-list.html',
  styleUrl: './film-list.css',
})
export class FilmList implements OnInit {
  films: any[] = [];

  constructor(
    private filmService: FilmService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.filmService.getTuttiFilm().subscribe({
      next: (data) => {
        this.films = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Errore nel caricamento film:', err),
    });
  }

  vaiAiDettagli(id: number) {
    this.router.navigate(['/film-detail', id]);
  }
}
