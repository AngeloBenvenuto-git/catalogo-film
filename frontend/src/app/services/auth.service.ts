import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';
  // Controlliamo entrambi i "cassetti" all'avvio
  private loggedIn = new BehaviorSubject<boolean>(this.getToken() !== null);
  loggedIn$ = this.loggedIn.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password });
  }

  registra(username: string, email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/registra`, { username, email, password });
  }

  // AGGIUNTO: parametro ricordami per decidere dove salvare
  salvaToken(token: string, ricordami: boolean = false) {
    if (ricordami) {
      localStorage.setItem('token', token); // Resta dopo chiusura browser
    } else {
      sessionStorage.setItem('token', token); // Scompare alla chiusura browser
    }
    this.loggedIn.next(true);
  }

  // MODIFICATO: cerca il token ovunque sia stato salvato
  getToken(): string | null {
    return localStorage.getItem('token') || sessionStorage.getItem('token');
  }

  // MODIFICATO: pulisce tutto per sicurezza
  logout() {
    localStorage.removeItem('token');
    sessionStorage.removeItem('token');
    this.loggedIn.next(false);
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  // Funzioni di utility per estrarre dati dal JWT (rimaste uguali ma usano il nuovo getToken)
  getRuolo(): string | null {
    const token = this.getToken();
    if (!token) return null;
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.ruolo;
  }

  getEmail(): string | null {
    const token = this.getToken();
    if (!token) return null;
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub;
  }

  getUsername(): string | null {
    const token = this.getToken();
    if (!token) return null;
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.username || null;
  }
}
