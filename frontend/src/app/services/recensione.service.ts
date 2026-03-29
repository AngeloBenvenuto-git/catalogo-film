import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RecensioneService {

  private apiUrl = 'http://localhost:8080/api/recensioni';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders() {
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.authService.getToken()}`
      })
    };
  }

  getRecensioniFilm(filmId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/film/${filmId}`);
  }

  aggiungiRecensione(filmId: number, testo: string, voto: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/film/${filmId}`, { testo, voto }, this.getHeaders());
  }

  cancellaRecensione(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`, this.getHeaders());
  }
}
