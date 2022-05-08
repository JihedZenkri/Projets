package up.pi4.samples.javafx;

//import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.beans.value.ChangeListener;

public class Graph extends Application {
    NumberAxis xaxis;
    NumberAxis yaxis;

    @Override
    public void start(Stage primaryStage) throws Exception {
        /* Definition de nos axes */
        NumberAxis xaxis = new NumberAxis(-20., 20., 2); // axe x
        NumberAxis yaxis = new NumberAxis(-20., 20., 2); // axe y

        /* Nommage des axes */
        xaxis.setLabel("x");
        yaxis.setLabel("y=f(x)");

        xaxis.setTickLabelFill(Color.rgb(245, 215, 130));
        yaxis.setTickLabelFill(Color.rgb(245, 215, 130));
        Text logoApp = new Text("Calculatrice Graphique!");

        Text equationTexte = new Text("Rentre ton équation");
        equationTexte.setFill(Color.rgb(245, 215, 130));

        Text borneInfTexte = new Text("Rentre ta borne inférieure");
        borneInfTexte.setFill(Color.rgb(245, 215, 130));

        Text borneSupTexte = new Text("Rentre ta borne supérieure");
        borneSupTexte.setFill(Color.rgb(245, 215, 130));

        TextField borneInf = new TextField("");
        TextField borneSup = new TextField("");

        TextField saisieEquation = new TextField("");
        saisieEquation.setMinWidth(180);
        borneInf.setMinWidth(180);
        borneSup.setMinWidth(180);

        // Liste qui gère l'historique des fonctions rentrées par le client
        List<String> listeHistorique = new ArrayList<String>();
        LineChart graph = new LineChart(xaxis, yaxis);
        graph.setCreateSymbols(false); // Cacher les points
        graph.setPrefSize(800, 800);

        /**
         * bouton Actualiser efface les valeurs entrée par utilisateur
         */
        Button buttonClear = new Button("Actualiser");
        buttonClear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saisieEquation.clear();
                borneInf.clear();
                borneSup.clear();
            }
        });

        /**
         * bouton Historique donne une nouvelle fenetre de toute les fonctions dessiner
         * par l'utilisateur même celle supprimer
         */

        Button buttonHistorique = new Button("Historique");
        buttonHistorique.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                StackPane secondaryLayout = new StackPane();
                ListView listView = new ListView();
                int size = listeHistorique.size();
                int numero = 1;
                for (int i = 0; i < size; i++) {
                    listView.getItems().add("Fonction " + numero + " : " + listeHistorique.get(i));
                    numero++;
                }
                secondaryLayout.getChildren().add(listView);
                Scene secondScene = new Scene(secondaryLayout, 230, 100);
                secondScene.getStylesheets().add("styles2.css");
                // Nouvelle fenêtre
                Stage newWindow = new Stage();
                newWindow.setTitle("Historique");
                newWindow.setScene(secondScene);
                newWindow.initModality(Modality.WINDOW_MODAL);
                newWindow.initOwner(primaryStage);

                // Position de la deuxieme fenêtre par rapport à la premiere.
                newWindow.setX(primaryStage.getX() + 200);
                newWindow.setY(primaryStage.getY() + 100);
                newWindow.show();
            }
        });

        /**
         * Button qui supprime les courbes tracer sur notre graph
         * 
         */
        Button delete = new Button("supprimer");
        delete.setFocusTraversable(false);
        delete.setOnAction(event -> {
            graph.getData().clear();
        });

        /**
         * Bouton et slider qui gère le dezoom et le zoom
         */
        Button buttonDeZoom = new Button("Dézoomer");
        buttonDeZoom.setFocusTraversable(false);

        Slider slider = new Slider();

        slider.setMin(-5);
        slider.setMax(5);
        slider.setValue(0);

        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);

        slider.setBlockIncrement(1);

        // Slider qui s'occupe des valeurs du zoom.
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                xaxis.setAutoRanging(false);
                xaxis.setLowerBound(-4 + (int) slider.getValue());

                xaxis.setUpperBound(4 + (int) slider.getValue());
                xaxis.setTickUnit(3);

                yaxis.setAutoRanging(false);
                yaxis.setLowerBound(-4 + (int) slider.getValue());
                yaxis.setUpperBound(4 + (int) slider.getValue());
                yaxis.setTickUnit(3);
            }
        });

        /**
         * action de dezoom
         */
        buttonDeZoom.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                xaxis.setAutoRanging(false);
                xaxis.setLowerBound(-20);
                xaxis.setUpperBound(20);
                xaxis.setTickUnit(2);

                yaxis.setAutoRanging(false);
                yaxis.setLowerBound(-20);
                yaxis.setUpperBound(20);
                yaxis.setTickUnit(2);
            }
        });

        /**
         * Bouton qui s'occupe de dessiner la fonction demandée permet aussi de tracer
         * les fonction continue
         * 
         */

        Button buttonDraw = new Button("Dessiner");
        buttonDraw.setFocusTraversable(false);

        buttonDraw.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int inf = Integer.parseInt(borneInf.getText());
                int sup = Integer.parseInt(borneSup.getText());
                String chaine = saisieEquation.getText();
                listeHistorique.add(chaine);

                boolean prec = false;
                /**
                 * Le prec sert a verifier si on se situe dans le graph ou pas et ensuite on
                 * compare par rapport à notre position afin de voir s'il y'a un changement une
                 * serie courante qu'on ajoute dans une liste de serie pour ensuite les dessiner
                 * a la fin on dessine chacun des serie avec des couleurs prises aléatoirement
                 */

                XYChart.Series serie_cur = new XYChart.Series(); // serie courante
                ArrayList<XYChart.Series> vec_serie = new ArrayList<XYChart.Series>();// liste de serie

                for (double x = inf; x <= sup; x += 0.01) {
                    Evaluator eq = new Evaluator(saisieEquation.getText(), x);
                    double y = eq.getExpressionValue();
                    boolean dans_graph = y >= inf && y <= sup;

                    if ((dans_graph != prec)) {
                        if (!dans_graph) {
                            serie_cur.getData().add(new XYChart.Data<>(x, y));

                        } else {
                            if (x != inf) {
                                vec_serie.add(serie_cur);
                                serie_cur = new XYChart.Series();
                                double old_y = (new Evaluator(saisieEquation.getText(), x - 0.01)).getExpressionValue();
                                serie_cur.getData().add(new XYChart.Data<>(x - 0.01, old_y));

                            }
                            serie_cur.getData().add(new XYChart.Data<>(x, y));
                        }
                    } else {
                        if (dans_graph) {
                            serie_cur.getData().add(new XYChart.Data<>(x, y));
                        }
                    }
                    prec = dans_graph;
                }

                // Ajout des séries dans la liste.
                vec_serie.add(serie_cur);

                // Dessin des fonctions de la liste avec une couleur prise de facon aléatoire.
                String colorArray[] = { "#A9C8A9", "#725D78", "#FFA500", "#007FFF", "#7F00FF", "#C39797", "#992D22",
                        "#00563F", "#B4B7FF", "#0d8aa5", "#a50d71", "#d29f3d", "#d23d9f", "#3dd292", "#36252d" };
                Random r = new Random();
                int randomNmb = r.nextInt(15);
                String color = colorArray[randomNmb];
                for (XYChart.Series s : vec_serie) {
                    s.setName(chaine);
                    graph.getData().add(s);
                    s.getNode().lookup(".chart-series-line").setStyle("-fx-stroke:" + color + ";");
                }
            }
        });

        /**
         * Recuperation d'une police d'ecriture. si le fichier de police n'a pas pu être
         * recuperer envoie un message d'erreur sur le terminal
         */
        InputStream fontStream = Graph.class.getResourceAsStream("recoleta.ttf");
        if (fontStream != null) {
            Font bgFont = Font.loadFont(fontStream, 30);

            logoApp.setFont(bgFont);

        } else {
            System.out.println("Fichier de police vide ");
        }

        logoApp.setFill(Color.rgb(245, 215, 130));

        VBox parametres = new VBox(6);
        parametres.setPadding(new Insets(20, 20, 20, 20));
        parametres.getChildren().addAll(logoApp, equationTexte, saisieEquation, borneInfTexte, borneInf, borneSupTexte,
                borneSup);

        HBox h = new HBox(10); // Horizontal Spacing : 10
        h.setPadding(new Insets(20, 20, 20, 20));
        h.getChildren().addAll(buttonDraw, buttonClear, buttonDeZoom, delete);

        HBox histo = new HBox(10); // Horizontal Spacing : 10
        h.setPadding(new Insets(20, 20, 20, 20));
        h.getChildren().addAll(buttonHistorique);

        VBox partieDroite = new VBox(4);
        partieDroite.getChildren().addAll(parametres, h, histo, slider);

        VBox graphique = new VBox(5);
        graphique.setPadding(new Insets(10, 10, 10, 10));
        graphique.getChildren().addAll(graph);

        HBox ecran = new HBox(5);
        ecran.setPadding(new Insets(10, 10, 10, 10));
        ecran.getChildren().addAll(graphique, partieDroite);
        ecran.setBackground(new Background(new BackgroundFill(Color.rgb(43, 46, 74), null, null)));

        Scene scene = new Scene(ecran, ecran.getPrefWidth(), ecran.getPrefHeight());
        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Calculatrice Graphique");

        primaryStage.show();
        primaryStage.sizeToScene();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
