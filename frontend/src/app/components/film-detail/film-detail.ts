import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FilmService } from '../../services/film';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-film-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './film-detail.html',
  styleUrl: './film-detail.css'
})
export class FilmDetail implements OnInit {
  film: any;
  trailerUrl: SafeResourceUrl | null = null;

  constructor(
    private route: ActivatedRoute,
    private filmService: FilmService,
    private cdr: ChangeDetectorRef,
    private location: Location,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.filmService.getFilmById(id).subscribe({
      next: (res) => {
        this.film = res;
        if (this.film.tmdbId) {
          this.caricaTrailer(this.film.tmdbId);
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error("Errore nel recupero del film", err)
    });
  }

  caricaTrailer(tmdbId: number) {
    const apiKey = '9579a0d27a808bfbf8073387eefbddad';

    // Proviamo prima a cercare il trailer specifico tramite TMDB
    const url = `https://api.themoviedb.org/3/movie/${tmdbId}/videos?api_key=${apiKey}&language=it-IT`;

    fetch(url)
      .then(response => response.json())
      .then(data => {
        // Cerchiamo un video di tipo 'Trailer' su YouTube
        let video = data.results?.find((v: any) => v.type === 'Trailer' && v.site === 'YouTube');

        // Se non c'è in italiano, riproviamo in inglese (molto più probabile)
        if (!video) {
          return fetch(`https://api.themoviedb.org/3/movie/${tmdbId}/videos?api_key=${apiKey}&language=en-US`)
            .then(res => res.json());
        }
        return { results: [video] };
      })
      .then(data => {
        const video = data.results?.find((v: any) => v.type === 'Trailer' || v.type === 'Teaser');

        if (video && video.key) {
          // CASO 1: Abbiamo trovato il video specifico su TMDB
          this.trailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${video.key}`);
        } else {
          // CASO 2: TMDB non ha link. Usiamo la RICERCA AUTOMATICA di YouTube.
          // In questo modo apparirà il primo video pertinente per "[Titolo] trailer ufficiale"
          const queryRicerca = encodeURIComponent(`${this.film.titolo} trailer ufficiale`);
          this.trailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
            `https://www.youtube.com/embed?listType=search&list=${queryRicerca}`
          );
        }
        this.cdr.detectChanges();
      })
      .catch(err => {
        console.error("Errore trailer, fallback sulla ricerca", err);
        // Fallback estremo: ricerca generica se tutto il resto fallisce
        const queryRicerca = encodeURIComponent(`${this.film.titolo} trailer`);
        this.trailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
          `https://www.youtube.com/embed?listType=search&list=${queryRicerca}`
        );
        this.cdr.detectChanges();
      });
  }

  tornaIndietro() {
    this.location.back();
  }
}
