import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs'; // <--- AGGIUNTI of e tap

@Injectable({
  providedIn: 'root',
})
export class FilmService {
  private apiUrl = 'http://localhost:8080/api/film';

  // Questa è la memoria dove salveremo i film scaricati
  private filmCache: any[] = [];

  constructor(private http: HttpClient) {}

  getTuttiFilm(): Observable<any[]> {
    // Se abbiamo già i film in memoria, restituiscili SUBITO
    if (this.filmCache.length > 0) {
      return of(this.filmCache);
    }
    // Altrimenti scaricali e salvali nella memoria (tap)
    return this.http.get<any[]>(this.apiUrl).pipe(
      tap(film => this.filmCache = film)
    );
  }

  // Modifichiamo anche la ricerca per pulire la cache se necessario
  cercaFilm(titolo: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cerca?titolo=${titolo}`).pipe(
      tap(film => this.filmCache = film)
    );
  }

  getFilmById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  ordinaPerValutazione(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/ordina/valutazione`).pipe(
      tap(film => this.filmCache = film)
    );
  }

  ordinaPerAnno(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/ordina/anno`).pipe(
      tap(film => this.filmCache = film)
    );
  }

  // Opzionale: un metodo per forzare il ricaricamento (es. tasto refresh)
  resetCache() {
    this.filmCache = [];
  }
}
