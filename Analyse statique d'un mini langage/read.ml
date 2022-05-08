(* Module Read - reading Polish programs *)

open Types
open Common 

(* Gets the indentation amount of a line, 2 spaces by 2 *)
let rec get_indent pos line =
  match line with
  | "" :: "" :: line' -> 1 + get_indent pos line'
  | "" :: line' -> failwith ("Indentation problem - single space at line " ^ string_of_int pos)
  | _ -> 0

(* Removes line indentation *)
let rec remove_empty_chars line =
  match line with
  | "" :: "" :: line' -> remove_empty_chars line'
  | _ -> line

let is_else_or_comment_instr instr =
  String.equal instr "ELSE" || String.equal instr "COMMENT"

let read_lines name : (int * string) list =
  let file = open_in name in
  let read () = try Some (input_line file) with End_of_file -> None in
  let rec read_lines_aux list i =
    match read () with
    | Some s ->
        read_lines_aux ((i, s) :: list) (i + 1)
    | None ->
        close_in file;
        List.rev list
  in
  read_lines_aux [] 1

let rec read_expr expr =
  let rec read_expr_aux expr acc =
    if acc = 0 then expr
    else
      match expr with
      | str :: l -> (
          if string_is_op str then
            read_expr_aux l (acc + 1)
          else
            read_expr_aux l (acc - 1)
        )
      | _ -> failwith "Couldn't read expression"
  in
  match expr with
  | [] -> failwith "Empty expression"
  | str :: l ->
      if string_is_op str then Op (op_of_string str, read_expr l, read_expr (read_expr_aux l 1))
      else if string_is_int str then Num (int_of_string str)
      else Var str

(* Reads a cond *)
let read_cond cond =
  let rec read_cond_aux cond expr =
    match cond with
    | str :: l -> (
        if string_is_comp str then
          let expr_rev = List.rev expr in (read_expr expr_rev, comp_of_string str, read_expr l)
        else
          read_cond_aux l (str :: expr)
      )
    | _ -> failwith "Coudn't read condition"
  in
  read_cond_aux cond []

let read_list lines =
  let rec read_block block indent =
    match block with
    | (pos, expr) :: block' ->
        let l = String.split_on_char ' ' expr in
        let li = get_indent pos l in
        let l_no_empty_chars = remove_empty_chars l in
        if pos = 1 && li != 0 then
          failwith ("Depth issue at line " ^ string_of_int pos)
        else
          if (not (is_else_or_comment_instr (List.hd l_no_empty_chars))) && li = indent then (* Ignore comments & elses & check that indent is correct *)
            [(pos, read_instr l_no_empty_chars block' indent)] @ read_block block' indent
          else 
            if li >= indent then 
              read_block block' indent
            else
              []
    | _ -> []
  and read_instr instr block' indent =
    match instr with
    | name :: ":=" :: expr -> Set (name, read_expr expr)
    | "PRINT" :: expr -> Print (read_expr expr)
    | "READ" :: name :: _ -> Read name
    | "IF" :: cond -> If
          ( read_cond cond,
            read_block block' (indent + 1),
            read_else block' indent )
    | "WHILE" :: cond -> While (read_cond cond, read_block block' (indent + 1))
    | word :: _ -> failwith ("Invalid instruction: " ^ word ^ " unexpected")
    | _ -> failwith "Invalid instruction"
  and read_else block indent = 
    match block with
    | (pos, expr) :: block' ->
        let l = String.split_on_char ' ' expr in
        let l_no_empty_chars = remove_empty_chars l in
        if String.equal (List.hd l_no_empty_chars) "ELSE" && get_indent pos l = indent then
          read_block block' (indent + 1)
        else
          read_else block' indent (* Keep going until you find an "Else" *)
    | _ -> []
  in
  read_block lines 0

let read_polish (filename : string) : program = let l = read_lines filename in read_list l