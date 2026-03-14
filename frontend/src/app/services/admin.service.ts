import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getHeaders() {
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${this.authService.getToken()}`
      })
    };
  }

  getTuttiUtenti(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/utenti`, this.getHeaders());
  }

  bannaUtente(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/utenti/${id}/banna`, {}, this.getHeaders());
  }

  sbannaUtente(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/utenti/${id}/sbanna`, {}, this.getHeaders());
  }

  promuoviARedattore(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/utenti/${id}/promuovi`, {}, this.getHeaders());
  }

  eliminaUtente(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/utenti/${id}`, this.getHeaders());
  }
}
