import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FilmService } from '../../services/film';

@Component({
  selector: 'app-film-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './film-detail.html',
  styleUrl: './film-detail.css'
})
export class FilmDetail implements OnInit {
  film: any;

  constructor(
    private route: ActivatedRoute,
    private filmService: FilmService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.filmService.getFilmById(id).subscribe({
      next: (res) => {
        this.film = res;
        this.cdr.detectChanges();
      },
      error: (err) => console.error("Errore nel recupero del film", err)
    });
  }
}
