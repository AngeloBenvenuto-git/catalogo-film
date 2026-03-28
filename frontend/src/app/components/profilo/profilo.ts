import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profilo',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './profilo.html',
  styleUrls: ['./profilo.css']
})
export class ProfileComponent implements OnInit {
  username: string = '';
  email: string = '';
  avatarPreview: string | null = null;

  // Campi per la modifica password
  nuovaPassword: string = '';
  confermaPassword: string = '';

  // Variabili per il Toast personalizzato
  showToast: boolean = false;
  toastMessage: string = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername() || '';
    this.email = this.authService.getEmail() || '';
    const savedAvatar = localStorage.getItem('user_avatar_' + this.email);
    if (savedAvatar) {
      this.avatarPreview = savedAvatar;
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const base64Image = e.target.result;
        this.avatarPreview = base64Image;
        localStorage.setItem('user_avatar_' + this.email, base64Image);
      };
      reader.readAsDataURL(file);
    }
  }

  triggerToast(message: string) {
    this.toastMessage = message;
    this.showToast = true;
    // Scompare dopo 2.5 secondi
    setTimeout(() => {
      this.showToast = false;
    }, 2500);
  }

  salvaProfilo(): void {
    if (this.nuovaPassword !== '' || this.confermaPassword !== '') {
      if (this.nuovaPassword !== this.confermaPassword) {
        this.triggerToast('Le password non coincidono!');
        return;
      }
      if (this.nuovaPassword.length < 4) {
        this.triggerToast('La password deve avere almeno 4 caratteri!');
        return;
      }
    }

    localStorage.setItem('custom_username', this.username);
    this.authService.updateProfile(
      this.username,
      this.nuovaPassword || undefined,
      this.avatarPreview || undefined
    ).subscribe({
      next: (response) => {
        this.triggerToast('Profilo aggiornato con successo!');
        setTimeout(() => {
          window.location.href = '/';
        }, 1500);
      },
      error: (err) => {
        console.warn('Backend non raggiungibile o errore nel salvataggio.', err);
        this.triggerToast('Errore nel salvataggio definitivo (salvato localmente).');
        setTimeout(() => {
          window.location.href = '/';
        }, 1500);
      }
    });
  }
}
