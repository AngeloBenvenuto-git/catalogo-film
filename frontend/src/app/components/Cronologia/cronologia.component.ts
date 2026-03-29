import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-cronologia',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cronologia.component.html',
  styleUrl: './cronologia.component.css'
})
export class CronologiaComponent implements OnInit {
  filmVisti: any[] = [];

  ngOnInit() {
    this.caricaCronologia();
  }

  caricaCronologia() {
    const data = localStorage.getItem('netfilm_history');
    this.filmVisti = data ? JSON.parse(data) : [];
  }

  rimuoviSingolo(id: number) {
    this.filmVisti = this.filmVisti.filter(f => f.id !== id);
    localStorage.setItem('netfilm_history', JSON.stringify(this.filmVisti));
  }

  svuotaTutto() {
    if(confirm("Vuoi svuotare tutta la cronologia?")) {
      localStorage.removeItem('netfilm_history');
      this.filmVisti = [];
    }
  }
}
