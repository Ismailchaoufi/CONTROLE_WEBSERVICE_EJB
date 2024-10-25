import com.mediatheque.entities.Emprunt;
import com.mediatheque.entities.Media;
import com.mediatheque.entities.Utilisateur;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class UserOperationsBean {
    @PersistenceContext
    private EntityManager em;

    public List<Media> getMediasDisponibles() {
        return em.createQuery("SELECT m FROM Media m WHERE m.disponible = true", Media.class)
                .getResultList();
    }

    public Emprunt emprunterMedia(Long mediaId, Long userId) {
        Media media = em.find(Media.class, mediaId);
        Utilisateur user = em.find(Utilisateur.class, userId);

        if (media != null && media.isDisponible()) {
            Emprunt emprunt = new Emprunt();
            emprunt.setMedia(media);
            emprunt.setUtilisateur(user);
            emprunt.setDateEmprunt(new Date());

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 14);
            emprunt.setDateRetourPrevue(cal.getTime());

            media.setDisponible(false);
            em.persist(emprunt);
            return emprunt;
        }
        return null;
    }

    public void retournerMedia(Long empruntId) {
        Emprunt emprunt = em.find(Emprunt.class, empruntId);
        if (emprunt != null && !emprunt.isRendu()) {
            emprunt.setRendu(true);
            emprunt.getMedia().setDisponible(true);
            em.merge(emprunt);
        }
    }

    public List<Emprunt> getEmpruntsUtilisateur(Long userId) {
        return em.createQuery(
                        "SELECT e FROM Emprunt e WHERE e.utilisateur.id = :userId AND e.rendu = false",
                        Emprunt.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}