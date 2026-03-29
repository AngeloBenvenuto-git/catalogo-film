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
  private avatarSubject = new BehaviorSubject<string | null>(null);
  avatar$ = this.avatarSubject.asObservable();

  constructor(private http: HttpClient) {}
  setAvatar(base64: string | null) {
    this.avatarSubject.next(base64);
  }

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
    localStorage.removeItem('custom_username');
    localStorage.removeItem('user_avatar');
    this.setAvatar(null);
    this.loggedIn.next(false);
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  getUsername(): string | null {
    const email = this.getEmail();
    if (email) {
      const custom = localStorage.getItem('custom_username_' + email);
      if (custom) return custom;
    }
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

  updateProfile(username: string, password?: string, fotoBase64?: string): Observable<any> {
    const body: any = { username };
    if (password) body.password = password;
    if (fotoBase64) body.fotoBase64 = fotoBase64;

    return this.http.put(`${this.apiUrl}/user/update`, body, {
      headers: { 'Authorization': `Bearer ${this.getToken()}` }
    });
  }

  getMe(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`, {
      headers: { 'Authorization': `Bearer ${this.getToken()}` }
    });
  }
}
