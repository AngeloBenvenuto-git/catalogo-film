import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FilmService {
  private apiUrl = 'http://localhost:8080/api/film';

  constructor(private http: HttpClient) {}

  getTuttiFilm(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getFilmById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  cercaFilm(titolo: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cerca?titolo=${titolo}`);
  }

  ordinaPerValutazione(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/ordina/valutazione`);
  }

  ordinaPerAnno(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/ordina/anno`);
  }
}
