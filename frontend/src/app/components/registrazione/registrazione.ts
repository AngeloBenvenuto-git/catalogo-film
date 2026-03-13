import { RouterLink } from '@angular/router';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registrazione',
  imports: [FormsModule, RouterLink],
  templateUrl: './registrazione.html',
  styleUrl: './registrazione.css',
})
export class Registrazione {
  nome: string = '';
  email: string = '';
  password: string = '';
  confermaPassword: string = '';

  constructor(private router: Router) {}

  onRegistrazione() {
    if (this.password !== this.confermaPassword) {
      alert('Le password non coincidono!');
      return;
    }
    if (this.nome === '' || this.email === '' || this.password === '') {
      alert('Compila tutti i campi!');
      return;
    }
    alert('Registrazione effettuata! Ora puoi fare il login.');
    this.router.navigate(['/login']);
  }
}