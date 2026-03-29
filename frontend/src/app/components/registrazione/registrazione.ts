import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-registrazione',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registrazione.html',
  styleUrl: './registrazione.css',
})
export class Registrazione {
  username: string = '';
  email: string = '';
  password: string = '';
  errore: string = '';

  constructor(private router: Router, private authService: AuthService) {}

  onRegistrati() {
    if (!this.username || !this.email || !this.password) {
      this.errore = 'Per favore, compila tutti i campi.';
      return;
    }

    this.authService.registra(this.username, this.email, this.password).subscribe({
      next: () => {
        alert('Account creato con successo! Ora puoi accedere.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.errore = err.error?.errore || 'Errore durante la registrazione.';
      }
    });
  }
}
