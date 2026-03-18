import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  // Importante: aggiungiamo i moduli per gestire input (FormsModule) e chiamate API (HttpClientModule)
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './chatBot.html',
  styleUrl: './chatBot.css'
})
export class ChatBot {
  messaggio: string = '';
  chatOpen: boolean = false;

  // Lista dei messaggi: inizialmente c'è solo il saluto del bot
  messaggi: { testo: string, daUtente: boolean }[] = [
    { testo: "Ciao! Sono l'assistente NetFilm potenziato con IA. Chiedimi pure un consiglio su un film!", daUtente: false }
  ];

  constructor(private http: HttpClient) {}

  /**
   * Invia il messaggio al backend Java
   */
  inviaMessaggio() {
    if (!this.messaggio.trim()) return;

    // Aggiungi il messaggio dell'utente alla lista
    const testoInviato = this.messaggio;
    this.messaggi.push({ testo: testoInviato, daUtente: true });
    this.messaggio = '';

    // Chiamata POST al tuo controller Spring Boot
    this.http.post<any>('http://localhost:8080/api/chat', { message: testoInviato })
      .subscribe({
        next: (res) => {
          // La risposta dall'IA (Gemini) viene aggiunta alla chat
          this.messaggi.push({ testo: res.reply, daUtente: false });
          this.scrollaInBasso();
        },
        error: (err) => {
          console.error("Errore chat:", err);
          this.messaggi.push({
            testo: "Ops! Non riesco a connettermi al server. Verifica che il backend sia attivo sulla porta 8080.",
            daUtente: false
          });
        }
      });
  }

  /**
   * Utility per far scorrere la chat all'ultimo messaggio
   */
  private scrollaInBasso() {
    setTimeout(() => {
      const chatBody = document.querySelector('.chat-body');
      if (chatBody) {
        chatBody.scrollTop = chatBody.scrollHeight;
      }
    }, 100);
  }
}
