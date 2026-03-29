import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {
  private apiUrl = 'http://localhost:8080/api/favorites';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders() {
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.authService.getToken()}`
      })
    };
  }

  getFavorites(username: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${username}`, this.getHeaders());
  }

  addFavorite(username: string, filmId: number): Observable<any> {
    return this.http.post<any>(this.apiUrl, { username, filmId }, this.getHeaders());
  }

  removeFavorite(username: string, filmId: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${username}/${filmId}`, this.getHeaders());
  }
}
