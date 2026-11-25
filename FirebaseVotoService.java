package br.edu.unijorge.urnacidada.service;

import br.edu.unijorge.urnacidada.model.TipoCargo;
import br.edu.unijorge.urnacidada.model.TipoVoto;
import br.edu.unijorge.urnacidada.model.Voto;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Integração com Firebase Cloud Firestore com fallback automático em memória
 * caso a chave serviceAccountKey.json ainda não esteja configurada na máquina.
 */
public class FirebaseVotoService implements PersistenciaVoto {
    private static final String COLLECTION_NAME = "votos_oficina_2026";
    private Firestore db;
    private boolean modoNuvemAtivo = false;
    private final List<Voto> cacheLocal = new ArrayList<>();

    public FirebaseVotoService() {
        inicializarFirebase();
    }

    private void inicializarFirebase() {
        try {
            File credencial = new File("serviceAccountKey.json");
            if (!credencial.exists()) {
                System.out.println("[AVISO] Arquivo 'serviceAccountKey.json' não encontrado. Executando em modo local de demonstração.");
                return;
            }
            if (FirebaseApp.getApps().isEmpty()) {
                try (FileInputStream serviceAccount = new FileInputStream(credencial)) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                    FirebaseApp.initializeApp(options);
                }
            }
            this.db = FirestoreClient.getFirestore();
            this.modoNuvemAtivo = true;
            System.out.println("[OK] Conectado ao Firebase Cloud Firestore.");
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao conectar no Firebase. Usando fallback local: " + e.getMessage());
            this.modoNuvemAtivo = false;
        }
    }

    @Override
    public void registrarVoto(Voto voto) {
        cacheLocal.add(voto);
        if (modoNuvemAtivo && db != null) {
            try {
                db.collection(COLLECTION_NAME).add(voto.toFirestoreMap()).get();
            } catch (Exception e) {
                System.err.println("[ERRO] Falha ao gravar voto no Firestore: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Voto> listarVotos() {
        if (modoNuvemAtivo && db != null) {
            try {
                List<Voto> votosNuvem = new ArrayList<>();
                ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
                for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
                    TipoCargo cargo = TipoCargo.valueOf(doc.getString("cargo"));
                    TipoVoto tipo = TipoVoto.valueOf(doc.getString("tipoVoto"));
                    Long numLong = doc.getLong("numeroCandidato");
                    Integer num = numLong != null ? numLong.intValue() : null;
                    String nome = doc.getString("nomeCandidato");
                    votosNuvem.add(new Voto(cargo, tipo, num, nome));
                }
                return votosNuvem;
            } catch (Exception e) {
                System.err.println("[ERRO] Falha ao ler votos do Firestore: " + e.getMessage());
            }
        }
        return new ArrayList<>(cacheLocal);
    }

    @Override
    public Map<String, Integer> apurarResultados() {
        Map<String, Integer> contagem = new LinkedHashMap<>();
        List<Voto> votos = listarVotos();

        for (Voto v : votos) {
            String chave;
            if (v.getTipoVoto() == TipoVoto.NOMINAL) {
                chave = String.format("[%s] %d - %s", v.getCargo().name(), v.getNumeroCandidato(), v.getNomeCandidato());
            } else {
                chave = String.format("[%s] VOTO %s", v.getCargo().name(), v.getTipoVoto().name());
            }
            contagem.put(chave, contagem.getOrDefault(chave, 0) + 1);
        }
        return contagem;
    }

    @Override
    public String getModoOperacao() {
        return modoNuvemAtivo ? "Firebase Cloud Firestore (Online)" : "Memória Local (Fallback Offline)";
    }
}
