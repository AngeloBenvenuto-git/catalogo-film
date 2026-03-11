import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { FilmList } from './components/film-list/film-list';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, FilmList],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}