import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email: string = '';
  password: string = '';
  ricordami: boolean = false;
  errore: string = '';
  caricamento: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  async onLogin() {
    this.errore = '';
    this.caricamento = true;
    this.cdr.detectChanges();

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: this.email, password: this.password })
      });

      const data = await response.json();

      if (response.ok) {
        this.authService.salvaToken(data.token, this.ricordami);
        this.router.navigate(['/']);
      } else {
        this.errore = data.errore || 'Credenziali errate o utente bannato!';
      }
    } catch (e) {
      this.errore = 'Errore di connessione al server.';
    } finally {
      this.caricamento = false;
      this.cdr.detectChanges();
    }
  }
}
