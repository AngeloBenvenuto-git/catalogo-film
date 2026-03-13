import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router'; // Ho aggiunto Router qui
import { FilmService } from '../../services/film';

@Component({
  selector: 'app-film-list',
  standalone: true, // Assicurati che ci sia se usi gli imports
  imports: [CommonModule, RouterLink],
  templateUrl: './film-list.html',
  styleUrl: './film-list.css',
})
export class FilmList implements OnInit {
  films: any[] = [];

  // UNICO COSTRUTTORE: mettiamo sia il service che il router qui dentro
  constructor(
    private filmService: FilmService,
    private router: Router
  ) {}

  ngOnInit() {
    this.filmService.getTuttiFilm().subscribe({
      next: (data) => (this.films = data),
      error: (err) => console.error('Errore nel caricamento film:', err),
    });
  }

  vaiAiDettagli(id: number) {
    console.log("Navigo al film con ID:", id);
    // Naviga alla rotta del dettaglio
    this.router.navigate(['/film-detail', id]);
  }
}
