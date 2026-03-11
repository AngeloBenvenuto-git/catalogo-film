import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-film-list',
  imports: [CommonModule],
  templateUrl: './film-list.html',
  styleUrl: './film-list.css',
})
export class FilmList {
  films = [
    { id: 1, titolo: 'Il Padrino', anno: 1972, genere: 'Dramma', descrizione: 'La storia della famiglia Corleone.' },
    { id: 2, titolo: 'Inception', anno: 2010, genere: 'Fantascienza', descrizione: 'Un ladro che ruba segreti dai sogni.' },
    { id: 3, titolo: 'Interstellar', anno: 2014, genere: 'Fantascienza', descrizione: 'Un viaggio oltre i confini della galassia.' },
    { id: 4, titolo: 'The Dark Knight', anno: 2008, genere: 'Azione', descrizione: 'Batman affronta il Joker a Gotham City.' },
    { id: 5, titolo: 'Forrest Gump', anno: 1994, genere: 'Commedia', descrizione: 'La vita straordinaria di un uomo semplice.' },
    { id: 6, titolo: 'Schindler\'s List', anno: 1993, genere: 'Storico', descrizione: 'La storia vera di Oskar Schindler.' },
  ];
}