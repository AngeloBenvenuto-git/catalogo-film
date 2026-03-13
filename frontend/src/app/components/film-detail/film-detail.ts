import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FilmService } from '../../services/film';

@Component({
  selector: 'app-film-detail',
  standalone: true,
  templateUrl: './film-detail.html',
  styleUrl: './film-detail.css'
})
export class FilmDetail implements OnInit {
  film: any;

  constructor(
    private route: ActivatedRoute,
    private filmService: FilmService
  ) {}

  ngOnInit() {
    // Prendi l'id dall'URL
    const id = Number(this.route.snapshot.paramMap.get('id'));

    // Chiama il service per avere i dettagli
    this.filmService.getFilmById(id).subscribe({
      next: (res) => {
        this.film = res;
      },
      error: (err) => console.error("Errore nel recupero del film", err)
    });
  }
}
