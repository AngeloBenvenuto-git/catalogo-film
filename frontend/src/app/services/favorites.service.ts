import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FavoritesService {
  private apiUrl = 'http://localhost:8080/api/favorites';

  constructor(private http: HttpClient) {}

  addFavorite(username: string, filmId: number) {
    return this.http.post('http://localhost:8080/api/favorites', {
      username,
      filmId
    });
  }


  removeFavorite(username: string, filmId: number) {
    return this.http.delete(`${this.apiUrl}/${username}/${filmId}`);
  }

  getFavorites(username: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${username}`);
  }
}

