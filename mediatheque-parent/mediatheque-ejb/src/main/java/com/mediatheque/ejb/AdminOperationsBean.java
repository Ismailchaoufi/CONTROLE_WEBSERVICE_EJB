package com.mediatheque.ejb;

import com.mediatheque.entities.Emprunt;
import com.mediatheque.entities.Media;

import javax.annotation.PostConstruct;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateful
public class AdminOperationsBean {
    @PersistenceContext
    private EntityManager em;

    private List<Media> mediaCache;
    private List<Emprunt> empruntsCache;

    @PostConstruct
    public void init() {
        refreshCaches();
    }

    public void refreshCaches() {
        mediaCache = em.createQuery("SELECT m FROM Media m", Media.class).getResultList();
        empruntsCache = em.createQuery(
                "SELECT e FROM Emprunt e WHERE e.rendu = false",
                Emprunt.class).getResultList();
    }

    public void ajouterMedia(Media media) {
        media.setDisponible(true);
        em.persist(media);
        refreshCaches();
    }

    public void modifierMedia(Media media) {
        em.merge(media);
        refreshCaches();
    }

    public void supprimerMedia(Long mediaId) {
        Media media = em.find(Media.class, mediaId);
        if (media != null) {
            em.remove(media);
            refreshCaches();
        }
    }

    public List<Media> getAllMedia() {
        return mediaCache;
    }

    public List<Emprunt> getEmpruntsEnCours() {
        return empruntsCache;
    }

    @Remove
    public void cleanup() {
        mediaCache = null;
        empruntsCache = null;
    }
}
