import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { FavoritesService } from '../../services/favorites.service';
import { FilmService } from '../../services/film';
import { AuthService } from '../../services/auth.service';
import {Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import { Location } from '@angular/common';
import {forkJoin} from 'rxjs';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './favorites.component.html',
  styleUrls: ['./favorites.component.css']
})
export class FavoritesComponent implements OnInit {

  films: any[] = [];
  username : string = "" ;
  loading = true;
  favoritesRaw :any[] = [];
  filmsCompleti : any[] = [];

  constructor(
    private favoriteService: FavoritesService,
    private filmService: FilmService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private router: Router,
    private location: Location
  ) {}

  ngOnInit(): void {
    // 1. Recuperiamo lo username
    this.username = this.authService.getUsername() ?? "";

    if (this.username) {
      // 2. Carichiamo la lista dei preferiti (i codici ID)
      this.favoriteService.getFavorites(this.username).subscribe({
        next: (favs: any[]) => {
          this.favoritesRaw = favs;
          this.filmsCompleti = []; // Importante: puliamo la lista prima di riempirla

          if (favs.length === 0) {
            console.log("Nessun preferito trovato per questo utente.");
          }

          // 3. Trasformiamo gli ID in Film reali
          favs.forEach(f => {
            this.filmService.getFilmById(f.filmId).subscribe({
              next: (filmDettaglio) => {
                // Verifichiamo che il film non sia già stato aggiunto (evita doppioni visivi)
                if (!this.filmsCompleti.some(movie => movie.id === filmDettaglio.id)) {
                  this.filmsCompleti.push(filmDettaglio);
                  this.cdr.detectChanges(); // Ora che hai messo cdr nel costruttore, funziona!
                }
              },
              error: (err) => console.error("Errore recupero dettaglio film ID: " + f.filmId, err)
            });
          });
        },
        error: (err) => {
          console.error("Errore nel recupero della lista preferiti", err);
          // Se vedi errore 403 qui, significa che Java sta ancora bloccando la chiamata
        }
      });
    } else {
      console.warn("Utente non loggato: impossibile mostrare i preferiti.");
    }
  }
  // Metodo per aprire il film cliccando sulla locandina
  openFilm(id: number) {
    // Usiamo '/films' perché è quello che usa la tua Home per aprire i dettagli
    this.router.navigate(['/films', id]);
  }

  // Metodo per tornare alla pagina precedente
  goBack(): void {
    this.location.back();
  }
}


