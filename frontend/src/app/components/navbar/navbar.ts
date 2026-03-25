import { Component, OnInit, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, FormsModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  isLoggato: boolean = false;
  isScrolled: boolean = false;
  showSearch: boolean = false;

  searchQuery: string = '';
  genereSelezionato: string = '';
  ordinamento: string = '';
  ordinamentoAnno: string = '';

  constructor(
    public authService: AuthService,
    private router: Router,
    private eRef: ElementRef
  ) {}

  ngOnInit() {
    this.authService.loggedIn$.subscribe(stato => this.isLoggato = stato);
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.isScrolled = window.scrollY > 50;
  }

  @HostListener('document:click', ['$event'])
  clickout(event: any) {
    const searchElement = this.eRef.nativeElement.querySelector('.search-container');
    if (this.showSearch && searchElement && !searchElement.contains(event.target)) {
      this.showSearch = false;
    }
  }

  toggleSearch() {
    this.showSearch = !this.showSearch;
    if (this.showSearch) {
      setTimeout(() => (this.eRef.nativeElement.querySelector('.search-input') as HTMLElement)?.focus(), 100);
    }
  }

  private applicaTutto() {
    this.router.navigate(['/'], {
      queryParams: {
        q: this.searchQuery || null,
        g: this.genereSelezionato || null,
        ord: this.ordinamento || null,
        ordAnno: this.ordinamentoAnno || null
      },
      queryParamsHandling: 'merge'
    });
  }

  eseguiRicerca() {
    this.genereSelezionato = '';
    this.applicaTutto();
  }

  setGenere(genere: string) {
    this.genereSelezionato = genere;
    this.searchQuery = '';
    this.applicaTutto();
  }

  setOrdinamento(tipo: string) {
    this.ordinamento = tipo;
    this.ordinamentoAnno = '';
    this.applicaTutto();
  }

  setOrdinamentoAnno(tipo: string) {
    this.ordinamentoAnno = tipo;
    this.ordinamento = '';
    this.applicaTutto();
  }

  resetFiltri() {
    this.searchQuery = '';
    this.genereSelezionato = '';
    this.ordinamento = '';
    this.ordinamentoAnno = '';
    this.router.navigate(['/'], { queryParams: {}, queryParamsHandling: '' });
  }

  // UTILITY PER IL TEMPLATE
  getEmail(): string | null { return this.authService.getEmail(); }
  getRuolo(): string | null { return this.authService.getRuolo(); }

  // CORRETTO: Recupera l'username reale dal JWT tramite il servizio
  getUsername(): string | null { return this.authService.getUsername(); }

  logout() {
    this.authService.logout();
    this.isLoggato = false;
    this.router.navigate(['/login']);
  }
  goToFavorites() {
    this.router.navigate(['/favorites']); // Assicurati che il path sia corretto nelle tue rotte
  }
}
