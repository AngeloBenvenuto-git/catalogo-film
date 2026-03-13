import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  // Variabili collegate all'HTML via [(ngModel)]
  email: string = '';
  password: string = '';

  constructor(private router: Router) {}

  onLogin() {
    console.log("Tentativo di login con:", this.email);

    // Simulazione login per l'esame
    if (this.email === 'admin@test.it' && this.password === '1234') {
      alert('Login effettuato con successo!');
      this.router.navigate(['/film-list']); // Ti sposta al catalogo
    } else {
      alert('Credenziali errate! Usa admin@test.it e password 1234');
    }
  }
}
