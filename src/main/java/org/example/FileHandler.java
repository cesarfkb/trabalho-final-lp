package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileHandler {
    private int id, narq;
    private Path tempDir;

    public FileHandler(int id) {
        this.id = id;
        narq = 1;
        String nome = "gravacao" + id + "-";
        try {
            tempDir = Files.createTempDirectory(nome);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File criaArquivoTemp(){
        try {
            Path arq = Files.createTempFile(tempDir, "gravacao" + id + "N" + narq + "-", ".wav");
            narq++;
            return arq.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNArq() {
        return narq;
    }

    public ArrayList<String> getCaminho() {
        ArrayList<String> nomeFileHandler = new ArrayList<>();
        File[] arquivos = tempDir.toFile().listFiles();
        for (File arquivo : arquivos) {
            nomeFileHandler.add(arquivo.getPath());
        }
        return nomeFileHandler;
    }

    public void limpar() {
        File arquivos[] = tempDir.toFile().listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                arquivo.delete();
            }
        }
        tempDir.toFile().delete();
    }
}
