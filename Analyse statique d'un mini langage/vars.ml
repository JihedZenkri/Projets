(* Module Vars - static analysis of Polish vars *)

open Types
open Print
open Common

let extract_instrs p =
  let rec extract_instrs_aux p acc =
    match p with
    | (pos, instr) :: p' ->
        let instr_flattened =
          String.split_on_char '\n' (string_of_instr instr 0)
        in
        let rec instrs_flattened_aux l acc =
          match l with
          | [] -> acc
          | l :: l' ->
              instrs_flattened_aux l'
                ([ (String.trim l, contains (List.hd instr_flattened) "WHILE") ]
                @ acc)
        in
        let l = instrs_flattened_aux instr_flattened [] in
        extract_instrs_aux p' l @ acc
    | [] -> acc
  in
  List.rev (extract_instrs_aux p [])

let token_is_var s =
  not
    (string_is_op s || s = "READ" || s = "PRINT" || s = "IF" || s = "ELSE"
   || s = "COMMENT" || s = "WHILE" || string_is_int s || s = ":="
   || string_is_comp s || s = "")

let vars_polish (p : program) : unit =
  let gather_vars instr vars =
    let rec extract_vars cbc vs =
      match cbc with
      | [] -> vs
      | v :: cbc' ->
          extract_vars cbc'
            (if token_is_var v then append_if_extern v vs else vs)
    in
    let instr_cbc = String.split_on_char ' ' instr in
    extract_vars instr_cbc []
  in
  let check_var x instr =
    match String.split_on_char ' ' instr with
    | "READ" :: v :: _ when x = v -> true
    | v :: ":=" :: _ when x = v -> true
    | _ -> false
  in
  let vars_states vs instr in_while =
    let rec vars_states_aux vs instr acc =
      match vs with
      | [] -> acc
      | v :: vs' ->
          vars_states_aux vs' instr [ (v, check_var v instr && not in_while) ]
          @ acc
    in
    vars_states_aux vs instr []
  in
  let instrs = extract_instrs p in
  let rec gathers_vars_and_states instrs varsstates =
    match instrs with
    | [] -> varsstates
    | (i, in_while) :: i' ->
        gathers_vars_and_states i' (vars_states (gather_vars i []) i in_while)
        @ varsstates
  in
  let rec print_varsstates vss =
    match vss with
    | (v, b) :: vss' ->
        print_string (v ^ " " ^ string_of_bool b);
        print_newline ();
        print_varsstates vss'
    | [] -> print_newline ()
  in
  let varsstates = gathers_vars_and_states instrs [] in
  let add_to_undec vss =
    let rec add_to_undec_aux vss vls vssaux =
      match vss with
      | (v, b) :: vss' ->
          if List.mem v vls then add_to_undec_aux vss' vls vssaux
          else
            add_to_undec_aux vss' (append_if_extern v vls) [ (v, b) ] @ vssaux
      | [] -> vssaux
    in
    add_to_undec_aux vss [] []
  in
  let collect_vars varsstates all =
    let rec collect_vars_aux vss acc =
      match vss with
      | (v, b) :: vss' ->
          collect_vars_aux vss'
            (if all then append_if_extern v acc
            else if not b then append_if_extern v acc
            else acc)
      | [] -> acc
    in
    collect_vars_aux varsstates []
  in
  let revvarsstates = List.rev varsstates in
  let vars_all = collect_vars revvarsstates true in
  let vars_undec = collect_vars (add_to_undec revvarsstates) false in
  print_list (List.sort compare vars_all) " ";
  print_newline ();
  print_list (List.sort compare vars_undec) " ";
  print_newline ()
