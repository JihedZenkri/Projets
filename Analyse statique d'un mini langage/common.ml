(* Module Common - utilities shared by multiple modules *)

open Types

(* Adds v to l if v is not in l *)
let append_if_extern v l = if List.mem v l then l else [ v ] @ l

let explode char_array_of_string =
  let rec exp a b = if a < 0 then b else exp (a - 1) (char_array_of_string.[a] :: b) in
  exp (String.length char_array_of_string - 1) []

(*remove a from the list l *)
let rec remove l a =
  match l with
  | [] -> []
  | x :: l' -> if x <> a then [ x ] @ remove l' a else l'
  
(* Replaces a with b in list *)
let replace_in_list list a b =
  let rec replace_in_list_aux l a b acc =
    match l with
    | [] -> acc
    | x :: l' ->
        if a = x then replace_in_list_aux l' a b [ b ] @ acc
        else replace_in_list_aux l' a b [ x ] @ acc
  in
  replace_in_list_aux list a b []

(* Replaces a with b in a string *)
let replace str a b =
  let rec replace_aux str a b acc =
    match str with
    | [] -> acc
    | s :: s' ->
        if s = a then replace_aux s' a b (acc ^ String.make 1 b)
        else replace_aux s' a b (acc ^ String.make 1 s)
  in
  replace_aux (explode str) a b ""

(* Merges two lists *)
let merge l1 l2 =
  let rec merge_aux l1 l2 acc =
    match l2 with
    | [] -> l1 @ acc
    | a :: l2' ->
        if List.mem a l1 then merge_aux l1 l2' acc
        else merge_aux l1 l2' (acc @ [ a ])
  in
  merge_aux l1 l2 []

(* Prints a list *)
let rec print_list lst sep =
  match lst with
  | x :: lst' ->
      print_string (x ^ sep);
      print_list lst' sep
  | [] -> ()

(* Returns true if string s2 is contained in s1 *)
let contains s1 s2 =
  try
    let len = String.length s2 in
    for i = 0 to String.length s1 - len do
      if String.sub s1 i len = s2 then raise Exit
    done;
    false
  with Exit -> true

(* Takes the last occurence of every individual element in a list *)
let take_last list =
  let rec take_last_aux list used_vars acc =
    match list with
    | [] -> acc
    | (var, signs) :: l' ->
        if List.mem var used_vars then take_last_aux l' used_vars acc
        else take_last_aux l' ([ var ] @ used_vars) [ (var, signs) ] @ acc
  in
  take_last_aux list [] []

(* Conversions *)

(* Turns a string to an op if applicable *)
let op_of_string s_op =
  match s_op with
  | "+" -> Add
  | "-" -> Sub
  | "*" -> Mul
  | "/" -> Div
  | "%" -> Mod
  | _ -> failwith "Operation not recognized"

(* Turns a string into a comp if applicable *)
let comp_of_string s_comp =
  match s_comp with
  | "=" -> Eq
  | "<>" -> Ne
  | "<" -> Lt
  | "<=" -> Le
  | ">" -> Gt
  | ">=" -> Ge
  | _ -> failwith "Comparison not recognized"

(* Checks if a string maps to a valid op *)
let string_is_op (op : string) =
  match op with "+" | "-" | "*" | "/" | "%" -> true | _ -> false

(* Checks if a string maps to a valid comp *)
let string_is_comp (comp : string) =
  match comp with "<" | "=" | ">" | "<>" | "<=" | ">=" -> true | _ -> false

(* Checks if a string maps to a valid int *)
let string_is_int str =
  try
    int_of_string str |> ignore;
    true
  with Failure _ -> false
