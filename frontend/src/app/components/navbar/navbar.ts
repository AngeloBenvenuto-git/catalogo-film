import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Fondamentale per le direttive base
import { RouterLink, RouterLinkActive } from '@angular/router'; // Aggiunto RouterLinkActive

@Component({
  selector: 'app-navbar',
  standalone: true, // Indica che il componente si gestisce da solo
  imports: [CommonModule, RouterLink, RouterLinkActive], // Carichiamo i moduli necessari
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {}
