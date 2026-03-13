import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-registrazione',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registrazione.html',
  styleUrl: './registrazione.css', // Puoi lasciarlo vuoto perché usiamo styles.css
})
export class Registrazione {
  nome: string = '';
  email: string = '';
  password: string = '';

  constructor(private router: Router) {}

  onRegistrati() {
    console.log("Registrazione per:", this.nome);

    if (this.nome && this.email && this.password) {
      alert('Account creato con successo! Ora puoi accedere.');
      this.router.navigate(['/login']);
    } else {
      alert('Per favore, compila tutti i campi.');
    }
  }
}
