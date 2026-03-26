import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';
  private loggedIn = new BehaviorSubject<boolean>(this.getToken() !== null);
  loggedIn$ = this.loggedIn.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password });
  }

  registra(username: string, email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/registra`, { username, email, password });
  }

  salvaToken(token: string, ricordami: boolean = false) {
    if (ricordami) {
      localStorage.setItem('token', token);
    } else {
      sessionStorage.setItem('token', token);
    }
    this.loggedIn.next(true);
  }

  getToken(): string | null {
    return localStorage.getItem('token') || sessionStorage.getItem('token');
  }

  logout() {
    localStorage.removeItem('token');
    sessionStorage.removeItem('token');
    localStorage.removeItem('custom_username'); // Pulisce anche il nome personalizzato
    this.loggedIn.next(false);
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  // MODIFICATO: Priorità allo username salvato localmente per aggiornamento immediato Navbar
  getUsername(): string | null {
    const custom = localStorage.getItem('custom_username');
    if (custom) return custom;

    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.username || null;
    } catch (e) { return null; }
  }

  getEmail(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub;
    } catch (e) { return null; }
  }

  getRuolo(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.ruolo;
    } catch (e) { return null; }
  }

  // Salvataggio sul Backend
  updateProfile(username: string, password?: string): Observable<any> {
    const body: any = { username };
    if (password) body.password = password;

    return this.http.put(`${this.apiUrl}/user/update`, body, {
      headers: { 'Authorization': `Bearer ${this.getToken()}` }
    });
  }

  // Aggiornamento locale per sincronizzare i componenti
  updateLocalUsername(username: string) {
    localStorage.setItem('custom_username', username);
  }
}
