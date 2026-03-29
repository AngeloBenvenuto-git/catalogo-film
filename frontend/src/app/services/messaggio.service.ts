import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class MessaggioService {

  private apiUrl = 'http://localhost:8080/api/messaggi';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private getAuthHeaders() {
    return {
      headers: { 'Authorization': `Bearer ${this.authService.getToken()}` }
    };
  }

  inviaMessaggio(oggetto: string, testo: string): Observable<any> {
    return this.http.post<any>(this.apiUrl, { oggetto, testo }, this.getAuthHeaders());
  }

  getTuttiMessaggi(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, this.getAuthHeaders());
  }

  getMessaggiNonLetti(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/non-letti`, this.getAuthHeaders());
  }

  contaNonLetti(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/conta-non-letti`, this.getAuthHeaders());
  }

  segnaComeLetto(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/letto`, {}, this.getAuthHeaders());
  }

  cancellaMessaggio(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`, this.getAuthHeaders());
  }

  getMieiMessaggi(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/miei`, this.getAuthHeaders());
  }

  rispondiAlMessaggio(id: number, risposta: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/rispondi`, { risposta }, this.getAuthHeaders());
  }
}
