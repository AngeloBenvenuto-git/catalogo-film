import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './components/navbar/navbar';
import { ChatBot } from './components/chatBot/chatBot'; // L'importazione c'è, bene!

@Component({
  selector: 'app-root',
  standalone: true, // Assicurati che ci sia standalone se usi gli imports così
  imports: [RouterOutlet, Navbar, ChatBot], // <--- AGGIUNTO CHATBOT QUI!
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}
