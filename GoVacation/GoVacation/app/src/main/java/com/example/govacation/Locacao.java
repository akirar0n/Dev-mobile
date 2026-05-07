package com.example.govacation;

public class Locacao {

    private long idloc;
    private String tipoloc;
    private String titulo;
    private String imagem;
    private String descr;
    private double preco;
    private String localizacao;
    private int qtdhospedes;
    private String disp;

    public Locacao(long idloc, String tipoloc, String titulo, String imagem, String descr, double preco, String localizacao, int qtdhospedes, String disp) {
        this.idloc = idloc;
        this.tipoloc = tipoloc;
        this.titulo = titulo;
        this.imagem = imagem;
        this.descr = descr;
        this.preco = preco;
        this.localizacao = localizacao;
        this.qtdhospedes = qtdhospedes;
        this.disp = disp;
    }

    public long getIdloc() { return idloc; }
    public void setIdloc(long idloc) { this.idloc = idloc; }

    public String getTipoloc() { return tipoloc; }
    public void setTipoloc(String tipoloc) { this.tipoloc = tipoloc; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }

    public String getDescr() { return descr; }
    public void setDescr(String descr) { this.descr = descr; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public int getQtdhospedes() { return qtdhospedes; }
    public void setQtdhospedes(int qtdhospedes) { this.qtdhospedes = qtdhospedes; }

    public String getDisp() { return disp; }
    public void setDisp(String disp) { this.disp = disp; }
}