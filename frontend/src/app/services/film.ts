import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class FilmService {
  private apiUrl = 'http://localhost:8080/api/film';
  private filmCache: any[] = [];

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders() {
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.authService.getToken()}`
      })
    };
  }

  getTuttiFilm(): Observable<any[]> {
    if (this.filmCache.length > 0) {
      return of(this.filmCache);
    }
    return this.http.get<any[]>(this.apiUrl).pipe(
      tap(film => this.filmCache = film)
    );
  }

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

  modificaFilm(id: number, film: any): Observable<any> {
    this.filmCache = [];
    return this.http.put<any>(`${this.apiUrl}/${id}`, film, this.getHeaders());
  }

  eliminaFilm(id: number): Observable<any> {
    this.filmCache = [];
    return this.http.delete<any>(`${this.apiUrl}/${id}`, this.getHeaders());
  }

  resetCache() {
    this.filmCache = [];
  }
}
