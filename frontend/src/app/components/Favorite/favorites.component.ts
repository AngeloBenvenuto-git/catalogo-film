import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FavoritesService } from '../../services/favorites.service';
import { FilmService } from '../../services/film';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule, Location } from '@angular/common';
import { ListaCurataService } from '../../services/lista-curata.service';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './favorites.component.html',
  styleUrls: ['./favorites.component.css']
})
export class FavoritesComponent implements OnInit {

  films: any[] = [];
  username: string = '';
  loading = true;
  favoritesRaw: any[] = [];
  filmsCompleti: any[] = [];
  listeLiked: any[] = [];

  constructor(
    private favoriteService: FavoritesService,
    private filmService: FilmService,
    private authService: AuthService,
    private listaService: ListaCurataService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername() ?? '';

    if (this.username) {
      this.favoriteService.getFavorites(this.username).subscribe({
        next: (favs: any[]) => {
          this.favoritesRaw = favs;
          this.filmsCompleti = [];

          favs.forEach(f => {
            this.filmService.getFilmById(f.filmId).subscribe({
              next: (filmDettaglio) => {
                if (!this.filmsCompleti.some(movie => movie.id === filmDettaglio.id)) {
                  this.filmsCompleti.push(filmDettaglio);
                  this.cdr.detectChanges();
                }
              },
              error: (err) => console.error('Errore recupero film ID: ' + f.filmId, err)
            });
          });
        },
        error: (err) => console.error('Errore recupero preferiti', err)
      });

      this.listaService.getListeLiked().subscribe({
        next: (liste) => {
          this.listeLiked = liste;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Errore recupero liste liked', err)
      });
    }
  }

  openFilm(id: number) {
    this.router.navigate(['/films', id]);
  }

  goBack(): void {
    this.location.back();
  }
  rimuoviFilm(filmId: number) {
    if (confirm('Rimuovere questo film dai preferiti?')) {
      this.favoriteService.removeFavorite(this.username, filmId).subscribe({
        next: () => {
          this.filmsCompleti = this.filmsCompleti.filter(f => f.id !== filmId);
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Errore rimozione film', err)
      });
    }
  }

  rimuoviLike(listaId: number) {
    if (confirm('Togliere il like a questa lista?')) {
      this.listaService.toggleLike(listaId).subscribe({
        next: () => {
          this.listeLiked = this.listeLiked.filter(l => l.id !== listaId);
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Errore rimozione like', err)
      });
    }
  }
}
