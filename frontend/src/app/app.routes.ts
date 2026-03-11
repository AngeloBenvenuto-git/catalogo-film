import { Routes } from '@angular/router';
import { FilmList } from './components/film-list/film-list';
import { FilmDetail } from './components/film-detail/film-detail';

export const routes: Routes = [
  { path: '', component: FilmList },
  { path: 'films/:id', component: FilmDetail },
];