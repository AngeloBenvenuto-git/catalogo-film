import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service'; // Controlla che il percorso sia giusto

@Injectable({
  providedIn: 'root'
})
export class ListaCurataService {
  private apiUrl = 'http://localhost:8080/api/liste';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders() {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  getTutteLeListe(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  aggiungiFilmALista(listaId: number, filmId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${listaId}/film/${filmId}`, {}, { headers: this.getHeaders() });
  }

  rimuoviFilmDaLista(listaId: number, filmId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${listaId}/film/${filmId}`, { headers: this.getHeaders() });
  }

  toggleLike(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/like`, {}, { headers: this.getHeaders() });
  }

  cancellaLista(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  creaLista(lista: any): Observable<any> {
    return this.http.post(this.apiUrl, lista, { headers: this.getHeaders() });
  }

  updateLista(id: number, dati: any): Observable<any> {
    const headers = this.getHeaders().set('Content-Type', 'application/json');
    return this.http.put(`${this.apiUrl}/${id}`, dati, { headers: headers });
  }
}
