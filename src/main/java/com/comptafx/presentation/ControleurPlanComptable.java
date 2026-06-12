package com.comptafx.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la page du Plan Comptable Tunisien
 * Affiche les classes de comptes comme documentation de référence
 */
public class ControleurPlanComptable implements Initializable {
    
    @FXML private TableView<CompteClasse> planComptableTable;
    @FXML private TableColumn<CompteClasse, String> codeColumn;
    @FXML private TableColumn<CompteClasse, String> descriptionColumn;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> classComboBox;
    
    private final ObservableList<CompteClasse> compteClasses = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupComboBox();
        loadClasse1Data();
    }
    
    private void setupComboBox() {
        ObservableList<String> classes = FXCollections.observableArrayList(
            "Classe 1 : Comptes de capitaux propres et passifs non courants",
            "Classe 2 : Immobilisations",
            "Classe 3 : Stocks",
            "Classe 4 : Comptes de tiers"
        );
        classComboBox.setItems(classes);
        classComboBox.setValue("Classe 1 : Comptes de capitaux propres et passifs non courants");
    }
    
    @FXML
    private void handleClassSelection() {
        String selected = classComboBox.getValue();
        if (selected != null) {
            compteClasses.clear();
            if (selected.contains("Classe 1")) {
                loadClasse1Data();
            } else if (selected.contains("Classe 2")) {
                loadClasse2Data();
            } else if (selected.contains("Classe 3")) {
                loadClasse3Data();
            } else if (selected.contains("Classe 4")) {
                loadClasse4Data();
            }
        }
    }
    
    private void setupTable() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        planComptableTable.setItems(compteClasses);
        planComptableTable.setEditable(false);
    }
    
    private void loadClasse1Data() {
        // Classe 1 : Comptes de capitaux propres et passifs non courants
        
        // 10. Capital
        compteClasses.add(new CompteClasse("10", "Capital"));
        compteClasses.add(new CompteClasse("101", "Capital social"));
        compteClasses.add(new CompteClasse("1011", "Capital souscrit - non appelé"));
        compteClasses.add(new CompteClasse("1012", "Capital souscrit - appelé, non versé"));
        compteClasses.add(new CompteClasse("1013", "Capital souscrit - appelé, versé"));
        compteClasses.add(new CompteClasse("10131", "Capital non amorti"));
        compteClasses.add(new CompteClasse("10132", "Capital amorti"));
        compteClasses.add(new CompteClasse("1018", "Capital souscrit soumis à une réglementation particulière"));
        compteClasses.add(new CompteClasse("105", "Fonds de dotation"));
        compteClasses.add(new CompteClasse("108", "Compte de l'exploitant"));
        compteClasses.add(new CompteClasse("109", "Actionnaires, capital souscrit - non appelé"));
        
        // 11. Réserves et primes liées au capital
        compteClasses.add(new CompteClasse("11", "Réserves et primes liées au capital"));
        compteClasses.add(new CompteClasse("111", "Réserve légale"));
        compteClasses.add(new CompteClasse("112", "Réserves statutaires"));
        compteClasses.add(new CompteClasse("117", "Primes liées au capital"));
        compteClasses.add(new CompteClasse("1171", "Primes d'émission"));
        compteClasses.add(new CompteClasse("1172", "Primes de fusion"));
        compteClasses.add(new CompteClasse("1173", "Primes d'apport"));
        compteClasses.add(new CompteClasse("1174", "Primes de conversion d'obligation"));
        compteClasses.add(new CompteClasse("1178", "Autres compléments d'apport"));
        compteClasses.add(new CompteClasse("118", "Autres réserves"));
        compteClasses.add(new CompteClasse("1181", "Réserves pour fonds social"));
        compteClasses.add(new CompteClasse("119", "Avoirs des actionnaires"));
        
        // 12. Résultats reportés
        compteClasses.add(new CompteClasse("12", "Résultats reportés"));
        compteClasses.add(new CompteClasse("121", "Résultats reportés"));
        compteClasses.add(new CompteClasse("128", "Modifications comptables affectant les résultats reportés"));
        
        // 13. Résultat de l'exercice
        compteClasses.add(new CompteClasse("13", "Résultat de l'exercice"));
        compteClasses.add(new CompteClasse("131", "Résultat bénéficiaire"));
        compteClasses.add(new CompteClasse("135", "Résultat déficitaire"));
        
        // 14. Autres capitaux propres
        compteClasses.add(new CompteClasse("14", "Autres capitaux propres"));
        compteClasses.add(new CompteClasse("141", "Titres soumis à des réglementations particulières"));
        compteClasses.add(new CompteClasse("142", "Réserves réglementées & réserves soumises à un régime fiscal particulier"));
        compteClasses.add(new CompteClasse("1421", "Réserves indisponibles"));
        compteClasses.add(new CompteClasse("143", "Amortissements dérogatoires"));
        compteClasses.add(new CompteClasse("144", "Réserve spéciale de réévaluation"));
        compteClasses.add(new CompteClasse("145", "Subventions d'investissement"));
        compteClasses.add(new CompteClasse("1451", "Subventions d'investissement"));
        compteClasses.add(new CompteClasse("1458", "Autres subventions d'investissement"));
        compteClasses.add(new CompteClasse("1459", "Subventions d'investissement inscrites aux comptes de résultat"));
        compteClasses.add(new CompteClasse("147", "Compte du concédant"));
        
        // 15. Provisions pour risques & charges
        compteClasses.add(new CompteClasse("15", "Provisions pour risques & charges"));
        compteClasses.add(new CompteClasse("151", "Provisions pour risques"));
        compteClasses.add(new CompteClasse("1511", "Provisions pour litiges"));
        compteClasses.add(new CompteClasse("1512", "Provisions pour garanties données aux clients"));
        compteClasses.add(new CompteClasse("1513", "Provisions pour pertes sur marchés à achèvement futur"));
        compteClasses.add(new CompteClasse("1514", "Provisions pour amendes & pénalités"));
        compteClasses.add(new CompteClasse("1515", "Provisions pour pertes de change"));
        compteClasses.add(new CompteClasse("1518", "Autres provisions pour risques"));
        compteClasses.add(new CompteClasse("152", "Provisions pour charges à répartir sur plusieurs exercices"));
        compteClasses.add(new CompteClasse("1522", "Provisions pour grosses réparations"));
        compteClasses.add(new CompteClasse("153", "Provisions pour retraites et obligations similaires"));
        compteClasses.add(new CompteClasse("154", "Provisions d'origine réglementaire"));
        compteClasses.add(new CompteClasse("155", "Provisions pour impôts"));
        compteClasses.add(new CompteClasse("156", "Provisions pour renouvellement des immobilisations"));
        compteClasses.add(new CompteClasse("157", "Provisions pour amortissement"));
        compteClasses.add(new CompteClasse("158", "Autres provisions pour charges"));
        
        // 16. Emprunts & dettes assimilées
        compteClasses.add(new CompteClasse("16", "Emprunts & dettes assimilées"));
        compteClasses.add(new CompteClasse("161", "Emprunts obligataires (assorties de sûretés)"));
        compteClasses.add(new CompteClasse("1611", "Emprunts obligataires convertibles en actions"));
        compteClasses.add(new CompteClasse("1618", "Autres emprunts obligataires"));
        compteClasses.add(new CompteClasse("162", "Emprunts auprès des établissements financiers (assorties de sûretés)"));
        compteClasses.add(new CompteClasse("1621", "Emprunts bancaires"));
        compteClasses.add(new CompteClasse("1626", "Refinancements acquis"));
        compteClasses.add(new CompteClasse("163", "Emprunts auprès d'autres établissements financiers (assorties de sûretés)"));
        compteClasses.add(new CompteClasse("164", "Emprunts et dettes assorties de conditions particulières"));
        compteClasses.add(new CompteClasse("1641", "Avances bloquées pour augmentation du capital"));
        compteClasses.add(new CompteClasse("1642", "Avances reçues et comptes courants des associés bloqués"));
        compteClasses.add(new CompteClasse("1644", "Avances conditionnées de l'Etat & organismes internationaux"));
        compteClasses.add(new CompteClasse("165", "Emprunts non assorties de sûretés (à subdiviser selon l'ordre des comptes des emprunts)"));
        compteClasses.add(new CompteClasse("166", "Dettes rattachées à des participations"));
        compteClasses.add(new CompteClasse("1661", "Dettes rattachées à des participations (groupe)"));
        compteClasses.add(new CompteClasse("1662", "Dettes rattachées à des participations (hors groupe)"));
        compteClasses.add(new CompteClasse("1663", "Dettes rattachées à des sociétés en participation"));
        compteClasses.add(new CompteClasse("167", "Dépôts & cautionnements reçus"));
        compteClasses.add(new CompteClasse("168", "Autres emprunts et dettes"));
        compteClasses.add(new CompteClasse("1681", "Autres emprunts"));
        compteClasses.add(new CompteClasse("1685", "Crédit fournisseurs d'immobilisations"));
        compteClasses.add(new CompteClasse("1688", "Autres dettes non courantes"));
        
        // 17. Comptes de liaison des établissements & succursales
        compteClasses.add(new CompteClasse("17", "Comptes de liaison des établissements & succursales"));
        compteClasses.add(new CompteClasse("171", "Comptes des liaison des établissements"));
        compteClasses.add(new CompteClasse("176", "Biens & prestations de services échangés entre établissements (charges)"));
        compteClasses.add(new CompteClasse("177", "Biens & prestations de services échangés entre établissements (produits)"));
        
        // 18. Autres passifs non courants
        compteClasses.add(new CompteClasse("18", "Autres passifs non courants"));
        compteClasses.add(new CompteClasse("185", "Écarts de conversion"));
        compteClasses.add(new CompteClasse("188", "Autres"));
    }
    
    private void loadClasse2Data() {
        // Classe 2 : Immobilisations
        
        // 21. Immobilisations incorporelles
        compteClasses.add(new CompteClasse("21", "Immobilisations incorporelles"));
        compteClasses.add(new CompteClasse("211", "Investissements de recherche & de développement"));
        compteClasses.add(new CompteClasse("212", "Concessions de marques, brevets, licences, marques, procédés & valeurs similaires"));
        compteClasses.add(new CompteClasse("213", "Logiciels"));
        compteClasses.add(new CompteClasse("214", "Fonds commercial"));
        compteClasses.add(new CompteClasse("216", "Droit au bail"));
        compteClasses.add(new CompteClasse("218", "Autres immobilisations incorporelles"));
        
        // 22. Immobilisations corporelles
        compteClasses.add(new CompteClasse("22", "Immobilisations corporelles"));
        compteClasses.add(new CompteClasse("221", "Terrains"));
        compteClasses.add(new CompteClasse("2213", "Terrains nus"));
        compteClasses.add(new CompteClasse("2214", "Terrains aménagés"));
        compteClasses.add(new CompteClasse("2215", "Terrains bâtis"));
        compteClasses.add(new CompteClasse("2216", "Agencements & aménagements des terrains"));
        compteClasses.add(new CompteClasse("222", "Constructions"));
        compteClasses.add(new CompteClasse("2221", "Bâtiments"));
        compteClasses.add(new CompteClasse("2225", "Installations générales, agencements & aménagements des constructions"));
        compteClasses.add(new CompteClasse("2226", "Ouvrages d'infrastructure"));
        compteClasses.add(new CompteClasse("2227", "Constructions sur sol d'autrui"));
        compteClasses.add(new CompteClasse("223", "Installations techniques, matériel et outillage industriels"));
        compteClasses.add(new CompteClasse("2231", "Installations techniques"));
        compteClasses.add(new CompteClasse("2234", "Matériel industriel"));
        compteClasses.add(new CompteClasse("2235", "Outillage industriel"));
        compteClasses.add(new CompteClasse("2237", "Agencements & aménagements du matériel & outillage industriels"));
        compteClasses.add(new CompteClasse("224", "Matériel de transport"));
        compteClasses.add(new CompteClasse("2241", "Matériel de transport de biens"));
        compteClasses.add(new CompteClasse("2244", "Matériel de transport de personnes"));
        compteClasses.add(new CompteClasse("228", "Autres immobilisations corporelles"));
        compteClasses.add(new CompteClasse("2281", "Installations générales, agencements et aménagements divers"));
        compteClasses.add(new CompteClasse("2282", "Équipement de bureau"));
        compteClasses.add(new CompteClasse("2286", "Emballages récupérables identifiables"));
        
        // 23. Immobilisations en cours
        compteClasses.add(new CompteClasse("23", "Immobilisations en cours"));
        compteClasses.add(new CompteClasse("231", "Immobilisations incorporelles en cours"));
        compteClasses.add(new CompteClasse("232", "Immobilisations corporelles en cours"));
        compteClasses.add(new CompteClasse("237", "Avances & acomptes versés sur immobilisations incorporelles"));
        compteClasses.add(new CompteClasse("238", "Avances & acomptes versés sur commandes d'immobilisations corporelles"));
        
        // 24. Immobilisations à statut juridique particulier
        compteClasses.add(new CompteClasse("24", "Immobilisations à statut juridique particulier"));
        
        // 25. Participations & créances liées à des participations
        compteClasses.add(new CompteClasse("25", "Participations & créances liées à des participations"));
        compteClasses.add(new CompteClasse("251", "Titres de participation"));
        compteClasses.add(new CompteClasse("2511", "Actions"));
        compteClasses.add(new CompteClasse("2518", "Autres titres"));
        compteClasses.add(new CompteClasse("256", "Autres formes de participation"));
        compteClasses.add(new CompteClasse("257", "Créances rattachées à des participations"));
        compteClasses.add(new CompteClasse("2571", "Créances rattachées à des participations (groupe)"));
        compteClasses.add(new CompteClasse("2574", "Créances rattachées à des participations (hors groupe)"));
        compteClasses.add(new CompteClasse("2575", "Versements représentatifs d'apports non capitalisés (appel de fonds)"));
        compteClasses.add(new CompteClasse("2576", "Avances consolidables"));
        compteClasses.add(new CompteClasse("2577", "Autres créances rattachées à des participations"));
        compteClasses.add(new CompteClasse("258", "Créances rattachées à des sociétés en participation"));
        compteClasses.add(new CompteClasse("259", "Versements restant à effectuer sur titres de participation non libérés"));
        
        // 26. Autres immobilisations financières
        compteClasses.add(new CompteClasse("26", "Autres immobilisations financières"));
        compteClasses.add(new CompteClasse("261", "Titres immobilisés (droit de propriété)"));
        compteClasses.add(new CompteClasse("2611", "Actions"));
        compteClasses.add(new CompteClasse("2618", "Autres titres"));
        compteClasses.add(new CompteClasse("262", "Titres immobilisés (droit de créance)"));
        compteClasses.add(new CompteClasse("2621", "Obligations"));
        compteClasses.add(new CompteClasse("2622", "Bons"));
        compteClasses.add(new CompteClasse("264", "Prêts"));
        compteClasses.add(new CompteClasse("2641", "Prêts participatifs"));
        compteClasses.add(new CompteClasse("2642", "Prêts aux associés"));
        compteClasses.add(new CompteClasse("2643", "Prêts au personnel"));
        compteClasses.add(new CompteClasse("2645", "Prêts assortis de sûretés (à subdiviser)"));
        compteClasses.add(new CompteClasse("2648", "Autres prêts"));
        compteClasses.add(new CompteClasse("265", "Dépôts et cautionnements versés"));
        compteClasses.add(new CompteClasse("2651", "Dépôts"));
        compteClasses.add(new CompteClasse("2655", "Cautionnements"));
        compteClasses.add(new CompteClasse("2656", "Dépôts bancaires non courants"));
        compteClasses.add(new CompteClasse("2658", "Autres"));
        compteClasses.add(new CompteClasse("266", "Autres créances immobilisées"));
        compteClasses.add(new CompteClasse("2661", "Créances immobilisées"));
        compteClasses.add(new CompteClasse("2667", "Créances diverses"));
        compteClasses.add(new CompteClasse("2668", "Autres créances non courantes"));
        compteClasses.add(new CompteClasse("269", "Versements restant à effectuer sur titres immobilisés non libérés"));
        
        // 27. Autres actifs non courants
        compteClasses.add(new CompteClasse("27", "Autres actifs non courants"));
        compteClasses.add(new CompteClasse("271", "Frais préliminaires"));
        compteClasses.add(new CompteClasse("272", "Charges à répartir"));
        compteClasses.add(new CompteClasse("273", "Frais d'émission et primes de remboursement des emprunts"));
        compteClasses.add(new CompteClasse("275", "Écarts de conversion"));
        compteClasses.add(new CompteClasse("278", "Autres"));
        
        // 28. Amortissements des immobilisations
        compteClasses.add(new CompteClasse("28", "Amortissements des immobilisations"));
        compteClasses.add(new CompteClasse("281", "Amortissements des immobilisations incorporelles (même ventilation que celle du compte 21)"));
        compteClasses.add(new CompteClasse("282", "Amortissements des immobilisations corporelles (même ventilation que celle du compte 22)"));
        compteClasses.add(new CompteClasse("284", "Amortissements des immobilisations à statut juridique particulier"));
        
        // 29. Provisions pour dépréciation des immobilisations
        compteClasses.add(new CompteClasse("29", "Provisions pour dépréciation des immobilisations"));
        compteClasses.add(new CompteClasse("291", "Provisions pour dépréciation des immobilisations incorporelles (même ventilation que celle du compte 21)"));
        compteClasses.add(new CompteClasse("292", "Provisions pour dépréciation des immobilisations corporelles (même ventilation que celle du compte 22)"));
        compteClasses.add(new CompteClasse("293", "Provisions pour dépréciation des immobilisations en cours (même ventilation que celle du compte 23)"));
        compteClasses.add(new CompteClasse("294", "Provisions pour dépréciation des immobilisations à statut juridique particulier"));
        compteClasses.add(new CompteClasse("295", "Provisions pour dépréciation des participations et des créances liées à des participations (même ventilation que celle du compte 25)"));
        compteClasses.add(new CompteClasse("296", "Provisions pour dépréciation des autres immobilisations financières (même ventilation que celle du compte 26)"));
    }
    
    private void loadClasse3Data() {
        // Classe 3 : Stocks
        
        // 31. Matières premières & fournitures liées
        compteClasses.add(new CompteClasse("31", "Matières premières & fournitures liées"));
        compteClasses.add(new CompteClasse("311", "Matières premières"));
        compteClasses.add(new CompteClasse("313", "Fournitures"));
        compteClasses.add(new CompteClasse("317", "Autres"));
        
        // 32. Autres approvisionnements
        compteClasses.add(new CompteClasse("32", "Autres approvisionnements"));
        compteClasses.add(new CompteClasse("321", "Matières consommables"));
        compteClasses.add(new CompteClasse("322", "Fournitures consommables"));
        compteClasses.add(new CompteClasse("326", "Emballages"));
        compteClasses.add(new CompteClasse("327", "Autres"));
        
        // 33. En-cours de production de biens
        compteClasses.add(new CompteClasse("33", "En-cours de production de biens"));
        compteClasses.add(new CompteClasse("331", "Produits en cours"));
        compteClasses.add(new CompteClasse("335", "Travaux en cours"));
        
        // 34. En-cours de production de services
        compteClasses.add(new CompteClasse("34", "En-cours de production de services"));
        compteClasses.add(new CompteClasse("341", "Études en cours"));
        compteClasses.add(new CompteClasse("345", "Prestations de services en cours"));
        
        // 35. Stocks de produits
        compteClasses.add(new CompteClasse("35", "Stocks de produits"));
        compteClasses.add(new CompteClasse("351", "Produits intermédiaires"));
        compteClasses.add(new CompteClasse("355", "Produits finis"));
        compteClasses.add(new CompteClasse("357", "Produits résiduels"));
        
        // 37. Stocks de marchandises
        compteClasses.add(new CompteClasse("37", "Stocks de marchandises"));
        
        // 39. Provisions pour dépréciation des stocks
        compteClasses.add(new CompteClasse("39", "Provisions pour dépréciation des stocks (à ventiler selon la nomenclature de cette classe)"));
    }
    
    private void loadClasse4Data() {
        // Classe 4 : Comptes de tiers
        
        // 40. Fournisseurs & comptes rattachés
        compteClasses.add(new CompteClasse("40", "Fournisseurs & comptes rattachés"));
        compteClasses.add(new CompteClasse("401", "Fournisseurs d'exploitation"));
        compteClasses.add(new CompteClasse("4011", "Fournisseurs - achats de biens ou de prestations de services"));
        compteClasses.add(new CompteClasse("4017", "Fournisseurs - retenues de garantie"));
        compteClasses.add(new CompteClasse("403", "Fournisseurs d'exploitation - effets à payer"));
        compteClasses.add(new CompteClasse("404", "Fournisseurs d'immobilisations"));
        compteClasses.add(new CompteClasse("4041", "Fournisseurs - achats d'immobilisations"));
        compteClasses.add(new CompteClasse("4047", "Fournisseurs d'immobilisations - retenues de garantie"));
        compteClasses.add(new CompteClasse("405", "Fournisseurs d'immobilisations - effets à payer"));
        compteClasses.add(new CompteClasse("408", "Fournisseurs - factures non parvenues"));
        compteClasses.add(new CompteClasse("4081", "Fournisseurs d'exploitation"));
        compteClasses.add(new CompteClasse("4084", "Fournisseurs d'immobilisations"));
        compteClasses.add(new CompteClasse("4088", "Fournisseurs - intérêts courus"));
        compteClasses.add(new CompteClasse("409", "Fournisseurs débiteurs"));
        compteClasses.add(new CompteClasse("4091", "Fournisseurs - avances et acomptes versés sur commandes"));
        compteClasses.add(new CompteClasse("4096", "Fournisseurs - créances pour emballages et matériel à rendre"));
        compteClasses.add(new CompteClasse("4097", "Fournisseurs - autres avoirs"));
        compteClasses.add(new CompteClasse("40971", "Fournisseurs d'exploitation"));
        compteClasses.add(new CompteClasse("40974", "Fournisseurs d'immobilisations"));
        compteClasses.add(new CompteClasse("4098", "Rabais, remises, ristournes à obtenir et autres avoirs non encore reçus"));
        
        // 41. Clients & comptes rattachés
        compteClasses.add(new CompteClasse("41", "Clients & comptes rattachés"));
        compteClasses.add(new CompteClasse("411", "Clients"));
        compteClasses.add(new CompteClasse("4111", "Clients - ventes de biens ou de prestations de services"));
        compteClasses.add(new CompteClasse("4117", "Clients - retenues de garantie"));
        compteClasses.add(new CompteClasse("413", "Clients - effets à recevoir"));
        compteClasses.add(new CompteClasse("416", "Clients douteux ou litigieux"));
        compteClasses.add(new CompteClasse("417", "Créances sur travaux non encore facturables"));
        compteClasses.add(new CompteClasse("418", "Clients - produits non encore facturés (produits à recevoir)"));
        compteClasses.add(new CompteClasse("4181", "Factures à établir"));
        compteClasses.add(new CompteClasse("4188", "Intérêts courus"));
        compteClasses.add(new CompteClasse("419", "Clients créditeurs"));
        compteClasses.add(new CompteClasse("4191", "Clients - avances et acomptes reçus sur commandes"));
        compteClasses.add(new CompteClasse("4196", "Clients - dettes pour emballages et matériel consignés"));
        compteClasses.add(new CompteClasse("4197", "Clients - autres avoirs"));
        compteClasses.add(new CompteClasse("4198", "Rabais, remises, ristournes à accorder et autres avoirs à établir"));
        
        // 42. Personnel et comptes rattachés
        compteClasses.add(new CompteClasse("42", "Personnel et comptes rattachés"));
        compteClasses.add(new CompteClasse("421", "Personnel - avances et acomptes"));
        compteClasses.add(new CompteClasse("422", "Comités d'entreprises et autres organes représentatifs du personnel"));
        compteClasses.add(new CompteClasse("423", "Personnel, œuvres sociales"));
        compteClasses.add(new CompteClasse("425", "Personnel - rémunérations dues"));
        compteClasses.add(new CompteClasse("426", "Personnel - dépôts"));
        compteClasses.add(new CompteClasse("427", "Personnel - oppositions"));
        compteClasses.add(new CompteClasse("428", "Personnel - charges à payer & produits à recevoir"));
        compteClasses.add(new CompteClasse("4282", "Dettes provisionnées pour congés à payer"));
        compteClasses.add(new CompteClasse("4286", "Autres charges à payer"));
        compteClasses.add(new CompteClasse("4287", "Produits à recevoir"));
        
        // 43. Etat et collectivités publiques
        compteClasses.add(new CompteClasse("43", "Etat et collectivités publiques"));
        compteClasses.add(new CompteClasse("431", "Etat - subventions à recevoir"));
        compteClasses.add(new CompteClasse("432", "Etat, impôts et taxes retenus à la source"));
        compteClasses.add(new CompteClasse("433", "Opérations particulières avec l'Etat, les collectivités publiques, les organismes internationaux"));
        compteClasses.add(new CompteClasse("434", "Etat - impôts sur les bénéfices"));
        compteClasses.add(new CompteClasse("4341", "Retenue à la source"));
        compteClasses.add(new CompteClasse("4342", "Acomptes provisionnels"));
        compteClasses.add(new CompteClasse("4343", "Impôt à liquider"));
        compteClasses.add(new CompteClasse("4349", "Impôts différés"));
        compteClasses.add(new CompteClasse("435", "Obligations cautionnées"));
        compteClasses.add(new CompteClasse("436", "Etat - taxes sur le chiffre d'affaires"));
        compteClasses.add(new CompteClasse("4365", "Taxes sur le chiffre d'affaires à décaisser"));
        compteClasses.add(new CompteClasse("43651", "TVA à payer"));
        compteClasses.add(new CompteClasse("43658", "Autres taxes sur le chiffre d'affaires"));
        compteClasses.add(new CompteClasse("4366", "Taxes sur le chiffre d'affaires déductibles"));
        compteClasses.add(new CompteClasse("43662", "TVA sur immobilisations"));
        compteClasses.add(new CompteClasse("43663", "TVA transférée par d'autres entreprises"));
        compteClasses.add(new CompteClasse("43666", "TVA sur autres biens et services"));
        compteClasses.add(new CompteClasse("43667", "Crédit de TVA à reporter"));
        compteClasses.add(new CompteClasse("43668", "Autres taxes sur le chiffre d'affaires"));
        compteClasses.add(new CompteClasse("4367", "Taxes sur le chiffre d'affaires collectées par l'entreprise"));
        compteClasses.add(new CompteClasse("43671", "TVA collectée"));
        compteClasses.add(new CompteClasse("436711", "TVA collectée sur les débits"));
        compteClasses.add(new CompteClasse("436712", "TVA collectée sur les encaissements"));
        compteClasses.add(new CompteClasse("43678", "Autres taxes sur le chiffre d'affaires"));
        compteClasses.add(new CompteClasse("4368", "Taxes sur le chiffre d'affaires à régulariser ou en attente"));
        compteClasses.add(new CompteClasse("437", "Autres impôts, taxes et versements assimilés"));
        compteClasses.add(new CompteClasse("438", "Etat - charges à payer et produits à recevoir"));
        compteClasses.add(new CompteClasse("4382", "Charges fiscales sur congés à payer"));
        compteClasses.add(new CompteClasse("4386", "Autres charges à payer"));
        compteClasses.add(new CompteClasse("4387", "Produits à recevoir"));
        
        // 44. Sociétés du groupe & associés
        compteClasses.add(new CompteClasse("44", "Sociétés du groupe & associés"));
        compteClasses.add(new CompteClasse("441", "Groupe"));
        compteClasses.add(new CompteClasse("4411", "Créances et intérêts courus"));
        compteClasses.add(new CompteClasse("4412", "Dettes et intérêts à payer"));
        compteClasses.add(new CompteClasse("442", "Associés - comptes courants"));
        compteClasses.add(new CompteClasse("4421", "Principal"));
        compteClasses.add(new CompteClasse("4428", "Intérêts courus"));
        compteClasses.add(new CompteClasse("446", "Associés - opérations sur le capital"));
        compteClasses.add(new CompteClasse("447", "Associés - dividendes à payer"));
        compteClasses.add(new CompteClasse("448", "Associés - opérations faites en commun"));
        compteClasses.add(new CompteClasse("4481", "Opérations courantes"));
        compteClasses.add(new CompteClasse("4488", "Intérêts courus"));
        
        // 45. Débiteurs divers et Créditeurs divers
        compteClasses.add(new CompteClasse("45", "Débiteurs divers et Créditeurs divers"));
        compteClasses.add(new CompteClasse("452", "Créances sur cessions d'immobilisations"));
        compteClasses.add(new CompteClasse("453", "Sécurité sociale et autres organismes sociaux"));
        compteClasses.add(new CompteClasse("4531", "Organismes sociaux"));
        compteClasses.add(new CompteClasse("45311", "CNSS"));
        compteClasses.add(new CompteClasse("45318", "Autres"));
        compteClasses.add(new CompteClasse("4538", "Organismes sociaux - charges à payer et produits à recevoir"));
        compteClasses.add(new CompteClasse("45382", "Charges sociales sur congés à payer"));
        compteClasses.add(new CompteClasse("45386", "Autres charges à payer"));
        compteClasses.add(new CompteClasse("45387", "Produits à recevoir"));
        compteClasses.add(new CompteClasse("454", "Dettes sur acquisitions de valeurs mobilières de placement"));
        compteClasses.add(new CompteClasse("455", "Créances sur cessions de valeurs mobilières de placement"));
        compteClasses.add(new CompteClasse("457", "Autres comptes débiteurs ou créditeurs"));
        compteClasses.add(new CompteClasse("458", "Diverses charges à payer et produits à recevoir"));
        compteClasses.add(new CompteClasse("4586", "Charges à payer"));
        compteClasses.add(new CompteClasse("4587", "Produits à recevoir"));
        
        // 46. Comptes transitoires ou d'attente
        compteClasses.add(new CompteClasse("46", "Comptes transitoires ou d'attente"));
        compteClasses.add(new CompteClasse("461", "Compte d'attente"));
        compteClasses.add(new CompteClasse("465", "Différence de conversion sur éléments courants"));
        compteClasses.add(new CompteClasse("4651", "Différences de conversion actif"));
        compteClasses.add(new CompteClasse("4652", "Différences de conversion passif"));
        compteClasses.add(new CompteClasse("468", "Autres comptes transitoires"));
        
        // 47. Comptes de régularisation
        compteClasses.add(new CompteClasse("47", "Comptes de régularisation"));
        compteClasses.add(new CompteClasse("471", "Charges constatées d'avance"));
        compteClasses.add(new CompteClasse("472", "Produits constatés d'avance"));
        compteClasses.add(new CompteClasse("478", "Comptes de répartition périodique de charges et produits"));
        compteClasses.add(new CompteClasse("4786", "Charges"));
        compteClasses.add(new CompteClasse("4787", "Produits"));
        
        // 48. Provisions courantes pour risques et charges
        compteClasses.add(new CompteClasse("48", "Provisions courantes pour risques et charges"));
        
        // 49. Provisions pour dépréciation des comptes de tiers
        compteClasses.add(new CompteClasse("49", "Provisions pour dépréciation des comptes de tiers"));
        compteClasses.add(new CompteClasse("491", "Provisions pour dépréciation des comptes clients"));
        compteClasses.add(new CompteClasse("494", "Provisions pour dépréciation des comptes de groupe et associés"));
        compteClasses.add(new CompteClasse("4941", "Comptes du groupe"));
        compteClasses.add(new CompteClasse("4942", "Comptes courants des associés"));
        compteClasses.add(new CompteClasse("4948", "Opérations faites en commun"));
        compteClasses.add(new CompteClasse("495", "Provisions pour dépréciation des comptes de débiteurs divers"));
        compteClasses.add(new CompteClasse("4952", "Créances sur cession d'immobilisation"));
        compteClasses.add(new CompteClasse("4955", "Créances sur cession des valeurs mobilières de placement"));
        compteClasses.add(new CompteClasse("4957", "Autres comptes débiteurs"));
    }
    
    /**
     * Classe interne pour représenter une classe de compte
     */
    public static class CompteClasse {
        private final String code;
        private final String description;
        
        public CompteClasse(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
}

