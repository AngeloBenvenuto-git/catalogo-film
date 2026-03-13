import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FilmService } from '../../services/film';

@Component({
  selector: 'app-film-list',
  imports: [CommonModule, RouterLink],
  templateUrl: './film-list.html',
  styleUrl: './film-list.css',
})
export class FilmList implements OnInit {
  films: any[] = [];

  constructor(private filmService: FilmService) {}

  ngOnInit() {
    this.filmService.getTuttiFilm().subscribe({
      next: (data) => this.films = data,
      error: (err) => console.error('Errore nel caricamento film:', err)
    });
  }
}