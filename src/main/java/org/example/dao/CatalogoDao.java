package org.example.dao;

import org.example.entities.Catalogo;
import org.example.entities.Prestito;
import org.example.entities.Utente;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;

public class CatalogoDao {

    private EntityManager entityManager;

    public CatalogoDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Metodi di base per il DAO

    public Catalogo getElementoById(long id) {
        return entityManager.find(Catalogo.class, id);
    }

    public Utente getUtenteById(long id) {
        return entityManager.find(Utente.class, id);
    }

    public Prestito getPrestitoById(long id) {
        return entityManager.find(Prestito.class, id);
    }

    public void elimina(Catalogo elemento) {
        entityManager.getTransaction().begin();
        entityManager.remove(elemento);
        entityManager.getTransaction().commit();
    }

    //---- Metodi ----

    //1- + elemento
    public void aggiungiElemento(Catalogo elemento) {
        entityManager.getTransaction().begin();
        entityManager.persist(elemento);
        entityManager.getTransaction().commit();
    }

    public void aggiungiUtente(Utente utente) {
        entityManager.getTransaction().begin();
        entityManager.persist(utente);
        entityManager.getTransaction().commit();
    }

    public void aggiungiPrestito(Prestito prestito) {
        entityManager.getTransaction().begin();
        entityManager.persist(prestito);
        entityManager.getTransaction().commit();
    }

    //2- - tramite ISBN
    public void rimuoviElementoPerIsbn(String isbn) {
        Query query = entityManager.createQuery("SELECT e FROM ElementoCatalogo e WHERE e.isbn = :isbn");
        query.setParameter("isbn", isbn);
        Catalogo elemento = (Catalogo) query.getSingleResult();
        entityManager.getTransaction().begin();
        entityManager.remove(elemento);
        entityManager.getTransaction().commit();
        System.out.println("Questoo ISBN: " + isbn + " è stato rimosso");
    }

    //3- Ricerca per ISBN
    public Catalogo trovaPerIsbn(String isbn) {
        Query query = entityManager.createQuery("SELECT e FROM ElementoCatalogo e WHERE e.isbn = :isbn");
        query.setParameter("isbn", isbn);
        System.out.println("queso con ISBN " + isbn + " è stato trovato:");
        return (Catalogo) query.getSingleResult();
    }

    //4- Ricerca per anno di pubblicazione
    public List<Catalogo> trovaPerAnno(int anno) {
        Query query = entityManager.createQuery("SELECT e FROM ElementoCatalogo e WHERE e.annoPubblicazione = :anno");
        query.setParameter("anno", anno);
        System.out.println("Elementi trovati con anno di pubblicazione " + anno + " :");
        return query.getResultList();
    }

    //5- Ricerca per autore
    public List<Catalogo> trovaPerAutore(String nomeAutore) {
        Query query = entityManager.createQuery("SELECT e FROM ElementoCatalogo e WHERE e.autore = :nomeAutore AND TYPE(e) = Libro");
        query.setParameter("nomeAutore", nomeAutore);
        System.out.println("Elementi con autore " + nomeAutore + " :");
        return query.getResultList();
    }

    //6- Ricerca per titolo, anche parziale
    public List<Catalogo> trovaPerTitolo(String titolo) {
        Query query = entityManager.createQuery("SELECT e FROM ElementoCatalogo e WHERE e.titolo LIKE :titolo");
        query.setParameter("titolo", "%" + titolo + "%");
        System.out.println("Elementi contengono la parola " + titolo + " :");
        return query.getResultList();
    }

    //7- Ricerca di elementi attualmente in prestito tramite il numero di tessera dell'utente
    public List<Catalogo> trovaElementiInPrestito(long numeroTessera) {
        Query query = entityManager.createQuery("SELECT e FROM Prestito p JOIN p.elementiPrestati e WHERE p.utente.numeroDiTessera = :numeroTessera AND p.dataRestituzioneEffettiva IS NULL");
        query.setParameter("numeroTessera", numeroTessera);
        System.out.println("Elementi  prestito per il numero di tessera: " + numeroTessera);
        return query.getResultList();
    }

    //8- Ricerca di prestiti scaduti e non restituiti
    public List<Prestito> trovaPrestitiScaduti() {
        LocalDate oggi = LocalDate.now();
        Query query = entityManager.createQuery("SELECT p FROM Prestito p WHERE p.dataRestituzioneEffettiva IS NULL AND p.dataRestituzionePrevista < :oggi");
        query.setParameter("oggi", oggi);
        System.out.println("Prestiti scaduti e non restituiti:");
        return query.getResultList();
    }
}
