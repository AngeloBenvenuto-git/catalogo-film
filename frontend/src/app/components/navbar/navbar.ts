import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  isLoggato: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.authService.loggedIn$.subscribe(stato => {
      this.isLoggato = stato;
    });
  }

  getEmail(): string | null {
    return this.authService.getEmail();
  }

  getRuolo(): string | null {
    return this.authService.getRuolo();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
