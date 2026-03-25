import { Component, OnInit, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FilmService } from '../../services/film';
import { AuthService } from '../../services/auth.service';
import { RecensioneService } from '../../services/recensione.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { GoogleMapsModule } from '@angular/google-maps';
import { FavoritesService } from '../../services/favorites.service';

// Importa il modulo delle mappe se non lo hai già fatto nel modulo principale,
// ma qui ci serve la dichiarazione globale per l'SDK JS
declare var google: any;

@Component({
  selector: 'app-film-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './film-detail.html',
  styleUrl: './film-detail.css'
})
export class FilmDetail implements OnInit, AfterViewInit {
  film: any;
  trailerUrl: SafeResourceUrl | null = null;
  modalitaModifica: boolean = false;
  filmModifica: any = {};
  recensioni: any[] = [];
  nuovaRecensione: string = '';
  nuovoVoto: number = 5;

  // Variabili cinema
  citta: string = '';
  cinema: any[] = [];
  caricamentoCinema: boolean = false;
  erroreCinema: string = '';

  // Variabili per Google Maps
  cinemas: any[] = [];
  center: { lat: number, lng: number } = { lat: 41.9028, lng: 12.4964 }; // Default su Roma
  zoom = 12;
  private readonly MAPS_KEY = 'AIzaSyAGWTr9oVHvICUVMgWrmdTbDPDy9CYceBs';
  private mappa: any = null;
  private markers: any[] = [];

  //variabili per i preferiti
  favorites: number[] = [];
  animatedFilmId: number | null = null;
  message: string = "";

  private messageTimeout: any

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private filmService: FilmService,
    private authService: AuthService,
    private recensioneService: RecensioneService,
    private cdr: ChangeDetectorRef,
    private location: Location,
    private sanitizer: DomSanitizer,
    private favoriteService: FavoritesService,
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    // Carico il film
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

    // Carico recensioni
    this.caricaRecensioni(id);

    // Controllo preferiti SOLO se loggata
    const username = this.authService.getUsername();
    if (username) {
      this.favoriteService.getFavorites(username).subscribe(favs => {
        this.favorites = favs.map(f => f.filmId);
      });
    }

    // Avvio ricerca cinema
    this.ottieniPosizioneEIniziaRicerca();
  }

  ngAfterViewInit() {
    setTimeout(() => {
      const input = document.getElementById('inputCitta') as HTMLInputElement;
      if (input && (window as any).google) {
        const autocomplete = new (window as any).google.maps.places.Autocomplete(input, {
          types: ['(cities)'],
          componentRestrictions: { country: 'it' }
        });
        autocomplete.addListener('place_changed', () => {
          const place = autocomplete.getPlace();
          if (place.geometry?.location) {
            const lat = place.geometry.location.lat();
            const lng = place.geometry.location.lng();
            this.citta = place.name || place.formatted_address || this.citta;
            this.center = { lat, lng };
            this.aggiornaMappa(lat, lng);
            this.cercaCinemaPerCoordinate(lat, lng);
            this.cdr.detectChanges();
          }
        });
      }
    }, 1000);
  }

  // --- LOGICA GOOGLE MAPS & METODI MAPPA ---

  inizializzaMappa(lat: number, lng: number) {
    const container = document.getElementById('mappa-container');
    if (!container || !(window as any).google) return;
    this.mappa = new google.maps.Map(container, {
      center: { lat, lng },
      zoom: 11,
      mapTypeControl: false,
      streetViewControl: false,
    });
  }

  aggiornaMappa(lat: number, lng: number) {
    if (!this.mappa) {
      this.inizializzaMappa(lat, lng);
      return;
    }
    this.mappa.setCenter({ lat, lng });
    this.mappa.setZoom(11);
    // Rimuovi marker precedenti
    this.markers.forEach(m => m.setMap(null));
    this.markers = [];
  }

  aggiungiMarkers(cinemaList: any[]) {
    if (!this.mappa) return;
    // Rimuovi marker precedenti
    this.markers.forEach(m => m.setMap(null));
    this.markers = [];

    cinemaList.forEach(c => {
      const marker = new google.maps.Marker({
        position: { lat: c.lat, lng: c.lng },
        map: this.mappa,
        title: c.nome,
        icon: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png'
      });

      const infoWindow = new google.maps.InfoWindow({
        content: `<div style="color:#000"><strong>${c.nome}</strong><br>${c.indirizzo}</div>`
      });

      marker.addListener('click', () => {
        infoWindow.open(this.mappa, marker);
      });

      this.markers.push(marker);
    });
  }

  // --- METODI CINEMA ---

  ottieniPosizioneEIniziaRicerca() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude;
          const lng = position.coords.longitude;
          this.center = { lat, lng };
          this.geocodingInverso(lat, lng);
        },
        () => {
          console.warn('Geolocalizzazione non disponibile.');
        }
      );
    }
  }

  trovaCinema() {
    if (!this.citta.trim()) return;
    const geocodeUrl = `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(this.citta)}&key=${this.MAPS_KEY}&language=it&region=it&components=country:IT`;

    fetch(geocodeUrl)
      .then(res => res.json())
      .then(data => {
        if (data.status === 'OK' && data.results?.length) {
          const loc = data.results[0].geometry.location;
          this.center = { lat: loc.lat, lng: loc.lng };
          this.aggiornaMappa(loc.lat, loc.lng);
          this.cercaCinemaPerCoordinate(loc.lat, loc.lng);
        } else {
          this.erroreCinema = 'Città non trovata. Prova con un nome diverso.';
          this.cdr.detectChanges();
        }
      })
      .catch(() => {
        this.erroreCinema = 'Errore nella ricerca.';
        this.cdr.detectChanges();
      });
  }

  cercaCinemaPerCoordinate(lat: number, lng: number) {
    this.caricamentoCinema = true;
    this.erroreCinema = '';
    this.cinema = [];

    fetch(`http://localhost:8080/api/film/${this.film.id}/cinema?lat=${lat}&lng=${lng}`)
      .then(res => res.json())
      .then(data => {
        if (data.errore) {
          this.erroreCinema = data.errore;
        } else {
          this.cinema = Array.isArray(data) ? data : [data];
          this.aggiungiMarkers(this.cinema);
        }
        this.caricamentoCinema = false;
        this.cdr.detectChanges();
      })
      .catch(() => {
        this.erroreCinema = 'Errore nella ricerca cinema.';
        this.caricamentoCinema = false;
        this.cdr.detectChanges();
      });
  }

  geocodingInverso(lat: number, lng: number) {
    const url = `https://maps.googleapis.com/maps/api/geocode/json?latlng=${lat},${lng}&key=${this.MAPS_KEY}&language=it&result_type=locality`;
    fetch(url)
      .then(res => res.json())
      .then(data => {
        if (data.status === 'OK' && data.results?.length) {
          const nomeLocalita = data.results[0].address_components
            .find((c: any) => c.types.includes('locality'))?.long_name;
          if (nomeLocalita) {
            this.citta = nomeLocalita;
            this.cdr.detectChanges();
          }
        }
        setTimeout(() => {
          this.aggiornaMappa(lat, lng);
          this.cercaCinemaPerCoordinate(lat, lng);
        }, 1200);
      })
      .catch(() => {
        setTimeout(() => {
          this.aggiornaMappa(lat, lng);
          this.cercaCinemaPerCoordinate(lat, lng);
        }, 1200);
      });
  }

  // --- FINE METODI CINEMA ---

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
      .catch(() => {
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

  haGiaRecensito(): boolean {
    const username = this.authService.getUsername();
    return this.recensioni.some(r => r.usernameUtente === username);
  }

  isRecensioneUtente(usernameUtente: string): boolean {
    return this.authService.getUsername() === usernameUtente;
  }

  toggleFavorite(): void {
    const username = this.authService.getUsername();

    // 1. Caso: Non loggato
    if (!username) {
      this.message = "Accedi per aggiungere ai preferiti";
      this.showAddedMessage();
      this.cdr.detectChanges();
      return;
    }

    this.animatedFilmId = this.film.id;
    setTimeout(() => this.animatedFilmId = null, 300);

    if (this.isFavoriteFilm(this.film.id)) {
      // 2. Caso: Rimozione
      this.favoriteService.removeFavorite(username, this.film.id).subscribe({
        next: () => {
          this.favorites = this.favorites.filter(f => f !== this.film.id);
          this.message = "Rimosso dai preferiti"; // Messaggio specifico
          this.showAddedMessage();
          this.cdr.detectChanges();
        },
        error: (err) => console.error("Errore rimozione", err)
      });
    } else {
      // 3. Caso: Aggiunta
      this.favoriteService.addFavorite(username, this.film.id).subscribe({
        next: (res) => {
          if (res) {
            this.favorites.push(this.film.id);
            this.message = "Aggiunto ai preferiti"; // Messaggio specifico
            this.showAddedMessage();
            this.cdr.detectChanges();
          }
        },
        error: (err) => console.error("Errore salvataggio", err)
      });
    }
  }

  isFavoriteFilm(filmId: number): boolean {
    return this.favorites.includes(filmId);
  }

  showAddedMessage(): void {
    // Se c'è già un timer attivo, lo cancelliamo per far partire quello nuovo
    if (this.messageTimeout) {
      clearTimeout(this.messageTimeout);
    }

    // Il messaggio sparirà dopo 3 secondi (3000ms)
    this.messageTimeout = setTimeout(() => {
      this.message = "";
      this.cdr.detectChanges(); // Fondamentale per far sparire il box dall'HTML
    }, 3000);
  }
}


