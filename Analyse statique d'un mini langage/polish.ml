(** Projet Polish -- Analyse statique d'un mini-langage impératif *)

(** Note : cet embryon de projet est pour l'instant en un seul fichier
    polish.ml. Il est recommandé d'architecturer ultérieurement votre
    projet en plusieurs fichiers source de tailles raisonnables *)

(*****************************************************************************)
(** Syntaxe abstraite Polish (types imposés, ne pas changer sauf extensions) *)
open Types 
open Read 
open Print 
open Eval 
open Simpl
open Vars
open Sign



let usage () =
  print_string "Polish : analyse statique d'un mini-langage\n";
  print_string "usage: à documenter (TODO)\n"

let main () =
  match Sys.argv with
  | [| _; "--reprint"; file |] -> print_polish (read_polish file)
  | [| _; "--eval"; file |] -> eval_polish (read_polish file)
  | [| _; "--simpl"; file |] -> simp_polish (read_polish file)
  | [| _; "--vars"; file |] -> vars_polish (read_polish file)
  | [| _; "--sign"; file |] -> sign_polish (read_polish file)


  | _ -> usage ()

(* lancement de ce main *)
let () = main ()
