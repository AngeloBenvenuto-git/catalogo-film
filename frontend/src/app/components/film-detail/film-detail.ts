import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FilmService } from '../../services/film';

@Component({
  selector: 'app-film-detail',
  imports: [CommonModule, RouterLink],
  templateUrl: './film-detail.html',
  styleUrl: './film-detail.css',
})
export class FilmDetail implements OnInit {
  film: any = null;

  constructor(
    private route: ActivatedRoute,
    private filmService: FilmService
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.filmService.getFilmById(id).subscribe({
      next: (data) => this.film = data,
      error: (err) => console.error('Errore nel caricamento film:', err)
    });
  }
}