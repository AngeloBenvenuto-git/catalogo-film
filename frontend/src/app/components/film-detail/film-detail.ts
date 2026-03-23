import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FilmService } from '../../services/film';
import { AuthService } from '../../services/auth.service';
import { RecensioneService } from '../../services/recensione.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { GoogleMapsModule } from '@angular/google-maps';
// Importa il modulo delle mappe se non lo hai già fatto nel modulo principale,
// ma qui ci serve la dichiarazione globale per l'SDK JS
declare var google: any;

@Component({
  selector: 'app-film-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule,GoogleMapsModule],
  templateUrl: './film-detail.html',
  styleUrl: './film-detail.css'
})
export class FilmDetail implements OnInit {
  film: any;
  trailerUrl: SafeResourceUrl | null = null;
  modalitaModifica: boolean = false;
  filmModifica: any = {};
  recensioni: any[] = [];
  nuovaRecensione: string = '';
  nuovoVoto: number = 5;
  citta: string = '';
  cinema: any[] = [];
  caricamentoCinema: boolean = false;
  erroreCinema: string = '';

  // Variabili per Google Maps
  cinemas: any[] = [];
  center: any = { lat: 41.9028, lng: 12.4964 }; // Default su Roma
  zoom = 12;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private filmService: FilmService,
    private authService: AuthService,
    private recensioneService: RecensioneService,
    private cdr: ChangeDetectorRef,
    private location: Location,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.filmService.getFilmById(id).subscribe({
      next: (res) => {
        this.film = res;
        this.filmModifica = { ...res };
        if (this.film.tmdbId) {
          this.caricaTrailer(this.film.tmdbId);
        }
        this.cdr.detectChanges();
      },
      error: (err) => console.error("Errore nel recupero del film", err)
    });
    this.caricaRecensioni(id);

    // Avvia la ricerca dei cinema vicini
    this.ottieniPosizioneEIniziaRicerca();
  }

  // --- LOGICA GOOGLE MAPS ---

  ottieniPosizioneEIniziaRicerca() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
        const lat = position.coords.latitude;
        const lng = position.coords.longitude;
        this.center = { lat, lng };
        this.caricaCinemaVicini(lat, lng);
      }, (error) => {
        console.warn("Geolocalizzazione non disponibile, uso posizione di default.");
        this.caricaCinemaVicini(this.center.lat, this.center.lng);
      });
    }
  }

  caricaCinemaVicini(lat: number, lng: number) {
    try {
      const posizione = new google.maps.LatLng(lat, lng);
      const service = new google.maps.places.PlacesService(document.createElement('div'));

      const request = {
        location: posizione,
        radius: '5000',
        type: ['movie_theater']
      };

      service.nearbySearch(request, (results: any, status: any) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {
          this.cinemas = results.map((place: any) => ({
            name: place.name,
            address: place.vicinity,
            rating: place.rating,
            position: {
              lat: place.geometry.location.lat(),
              lng: place.geometry.location.lng()
            },
            url: `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(place.name + ' ' + place.vicinity)}`
          }));
          this.cdr.detectChanges();
        }
      });
    } catch (e) {
      console.error("Errore SDK Google Maps:", e);
    }
  }

  // --- FINE LOGICA MAPS ---

  caricaRecensioni(filmId: number) {
    this.recensioneService.getRecensioniFilm(filmId).subscribe({
      next: (data) => {
        this.recensioni = [...data];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Errore caricamento recensioni', err)
    });
  }

  inviaRecensione() {
    if (!this.nuovaRecensione.trim()) return;
    this.recensioneService.aggiungiRecensione(this.film.id, this.nuovaRecensione, this.nuovoVoto).subscribe({
      next: () => {
        this.nuovaRecensione = '';
        this.nuovoVoto = 5;
        this.cdr.detectChanges();
        this.caricaRecensioni(this.film.id);
      },
      error: (err) => console.error('Errore invio recensione', err)
    });
  }

  cancellaRecensione(id: number) {
    if (confirm('Sei sicuro di voler eliminare questa recensione?')) {
      this.recensioneService.cancellaRecensione(id).subscribe({
        next: () => this.caricaRecensioni(this.film.id)
      });
    }
  }

  calcolaMediaVoti(): number {
    if (this.recensioni.length === 0) return 0;
    const somma = this.recensioni.reduce((acc, r) => acc + r.voto, 0);
    return somma / this.recensioni.length;
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  isAdminOrRedattore(): boolean {
    const ruolo = this.authService.getRuolo();
    return ruolo === 'ADMIN' || ruolo === 'REDATTORE';
  }

  isAdmin(): boolean {
    return this.authService.getRuolo() === 'ADMIN';
  }

  eliminaFilm() {
    if (confirm('Sei sicuro di voler eliminare questo film?')) {
      this.filmService.eliminaFilm(this.film.id).subscribe({
        next: () => {
          alert('Film eliminato con successo!');
          this.router.navigate(['/']);
        },
        error: (err) => console.error('Errore eliminazione film', err)
      });
    }
  }

  salvaModifica() {
    const filmDaModificare = {
      titolo: this.filmModifica.titolo,
      trama: this.filmModifica.trama,
      anno: this.filmModifica.anno,
      durata: this.filmModifica.durata,
      valutazione: this.filmModifica.valutazione,
      tipologia: this.filmModifica.tipologia,
      posterUrl: this.filmModifica.posterUrl,
      tmdbId: this.filmModifica.tmdbId
    };

    this.filmService.modificaFilm(this.film.id, filmDaModificare).subscribe({
      next: (res) => {
        this.film = res;
        this.modalitaModifica = false;
        this.cdr.detectChanges();
        alert('Film modificato con successo!');
      },
      error: (err) => console.error('Errore modifica film', err)
    });
  }

  caricaTrailer(tmdbId: number) {
    const apiKey = '9579a0d27a808bfbf8073387eefbddad';
    const url = `https://api.themoviedb.org/3/movie/${tmdbId}/videos?api_key=${apiKey}&language=it-IT`;

    fetch(url)
      .then(response => response.json())
      .then(data => {
        let video = data.results?.find((v: any) => v.type === 'Trailer' && v.site === 'YouTube');
        if (!video) {
          return fetch(`https://api.themoviedb.org/3/movie/${tmdbId}/videos?api_key=${apiKey}&language=en-US`)
            .then(res => res.json());
        }
        return { results: [video] };
      })
      .then(data => {
        const video = data.results?.find((v: any) => v.type === 'Trailer' || v.type === 'Teaser');
        if (video && video.key) {
          this.trailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(`https://www.youtube.com/embed/${video.key}`);
        } else {
          const queryRicerca = encodeURIComponent(`${this.film.titolo} trailer ufficiale`);
          this.trailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
            `https://www.youtube.com/embed?listType=search&list=${queryRicerca}`
          );
        }
        this.cdr.detectChanges();
      })
      .catch(err => {
        const queryRicerca = encodeURIComponent(`${this.film.titolo} trailer`);
        this.trailerUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
          `https://www.youtube.com/embed?listType=search&list=${queryRicerca}`
        );
        this.cdr.detectChanges();
      });
  }

  trovaCinema() {
    if (!this.citta.trim()) return;
    this.caricamentoCinema = true;
    this.erroreCinema = '';
    this.cinema = [];

    fetch(`http://localhost:8080/api/film/${this.film.id}/cinema?citta=${encodeURIComponent(this.citta)}`)
      .then(res => res.json())
      .then(data => {
        if (data.errore) {
          this.erroreCinema = 'Proiezione non disponibile in questa città. Ecco i cinema vicini:';
        }
        this.cinema = Array.isArray(data) ? data : [data];
        this.caricamentoCinema = false;
        this.cdr.detectChanges();
      })
      .catch(() => {
        this.erroreCinema = 'Errore nella ricerca cinema.';
        this.caricamentoCinema = false;
        this.cdr.detectChanges();
      });
  }

  tornaIndietro() {
    this.location.back();
  }

  haGiaRecensito(): boolean {
    const username = this.authService.getUsername();
    return this.recensioni.some(r => r.usernameUtente === username);
  }

  isRecensioneUtente(usernameUtente: string): boolean {
    return this.authService.getUsername() === usernameUtente;
  }

}
