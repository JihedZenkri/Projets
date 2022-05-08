(* Module Sign - sign list for all variables *)

open Types
open Common
open Eval

(* Converts a sign of its string counterpart *)
let string_of_sign s =
  match s with Pos -> "+" | Neg -> "-" | Zero -> "0" | Error -> "!"

(* Sign of an int *)
let sign_of_x x =
  match x with
  | 0 -> [ Zero ]
  | k when k > 0 -> [ Pos ]
  | k when k < 0 -> [ Neg ]
  | _ -> [ Error ]

(* s1 and s2 are signs lists *)
(* Signs of s1 + s2 *)
let sign_add s1 s2 =
  let rec sign_add_aux s1 s2 acc =
    match s2 with
    | [] -> acc
    | sgn :: s2' -> (
        match sgn with
        | Zero | Pos | Neg -> sign_add_aux s1 s2' (s1 @ acc)
        | Error -> sign_add_aux ([ Error ] @ s1) s2' [ Error ] @ acc
      )
  in
  sign_add_aux s1 s2 []

(* Reverses a signs list *)
let rev_sign s =
  if List.mem Pos s then replace_in_list s Pos Neg
  else if List.mem Neg s then replace_in_list s Neg Pos
  else s

(* Signs of s1 - s2 *)
let sign_sub s1 s2 = sign_add s1 (rev_sign s2)

(* Signs of s1 * s2 *)
let sign_mul s1 s2 =
  let rec sign_mul_aux s1 s2 acc =
    match s2 with
    | [] -> acc
    | sgn :: s2' -> (
        match sgn with
        | Zero -> sign_mul_aux s1 s2' [ Zero ] @ acc
        | Neg -> sign_mul_aux s1 s2' (rev_sign s1 @ acc)
        | Error -> sign_mul_aux ([ Error ] @ s1) s2' acc
        | Pos -> sign_mul_aux s1 s2' (s1 @ acc))
  in
  sign_mul_aux s1 s2 []

(* Signs of s1 / s2 *)
let sign_div s1 s2 =
  let rec sign_div_aux s1 s2 acc =
    match s2 with
    | [] -> acc
    | sgn :: s2' -> (
        match sgn with
        | Zero -> sign_div_aux ([ Error ] @ s1) s2' [ Error ] @ acc
        | Neg -> sign_div_aux s1 s2' (rev_sign s1 @ acc)
        | Error -> sign_div_aux ([ Error ] @ s1) s2' acc
        | Pos -> sign_div_aux s1 s2' (s1 @ acc))
  in
  sign_div_aux s1 s2 []

(* Signs of mod s1 mod s2 *)
let sign_mod s1 s2 = sign_div s1 s2

(* Signs of binops *)
let sign_op op =
  match op with
  | Add -> fun x y -> sign_add x y
  | Sub -> fun x y -> sign_sub x y
  | Mul -> fun x y -> sign_mul x y
  | Div -> fun x y -> sign_div x y
  | Mod -> fun x y -> sign_mod x y

(* Signs of > *)
let sign_sup s1 s2 =
  let rec sign_sup_aux s1 s2 acc =
    match s2 with
    | [] -> acc
    | sgn :: s2' -> (
        match sgn with
        | Error -> sign_sup_aux s1 s2' (s1 @ acc)
        | Neg -> sign_sup_aux s1 s2' (s1 @ acc)
        | Zero -> sign_sup_aux s1 s2' (remove (remove s1 Zero) Neg) @ acc
        | _ -> acc)
  in
  sign_sup_aux s1 s2 []

(* Signs of >= *)
let sign_supeq s1 s2 =
  let rec sign_supeq_aux s1 s2 acc =
    match s2 with
    | [] -> acc
    | sgn :: s2' -> (
        match sgn with
        | Error -> sign_supeq_aux s1 s2'(s1 @ acc)
        | Neg -> sign_supeq_aux s1 s2' (s1 @ acc)
        | Zero -> sign_supeq_aux s1 s2' (remove s1 Neg) @ acc
        | _ -> acc)
  in
  sign_supeq_aux s1 s2 []

(* Signs of < *)
let sign_inf s1 s2 =
  let rec sign_inf_aux s1 s2 acc =
    match s2 with
    | [] -> acc
    | sgn :: s2' -> (
        match sgn with
        | Error -> sign_inf_aux s1 s2' (s1 @ acc)
        | Neg -> sign_inf_aux s1 s2' (s1 @ acc)
        | Zero -> sign_inf_aux s1 s2' (remove (remove s1 Zero) Pos) @ acc
        | _ -> acc)
  in
  sign_inf_aux s1 s2 []

(* Signs of <=  *)
let sign_infeg s1 s2 =
  let rec sign_infeq_aux s1 s2 acc =
    match s2 with
    | [] -> acc
    | sgn :: s2' -> (
        match sgn with
        | Error -> sign_infeq_aux s1 s2' (s1 @ acc)
        | Neg -> sign_infeq_aux s1 s2' (s1 @ acc)
        | Zero -> sign_infeq_aux s1 s2' (remove s1 Pos) @ acc
        | Pos -> sign_infeq_aux s1 s2' (s1 @ acc)
      )
  in
  sign_infeq_aux s1 s2 []

(* Signs of =  *)
let sign_eg s1 s2 = if s1 = s2 then s1 else [ Error ]

(* Signs of <>  *)
let sign_diff s1 s2 = [ Pos; Neg; Zero; Error ]

(* Signs of an expression  *)
let rec sign_expr expr env_sign =
  match expr with
  | Num x -> sign_of_x x
  | Var s -> List.assoc s env_sign
  | Op (op, e1, e2) ->
      let s1 = sign_expr e1 env_sign in
      let s2 = sign_expr e2 env_sign in
      (sign_op op) s1 s2

(* Signs of a comparison *)
let sign_comp comp =
  match comp with
  | Eq -> fun x y -> sign_eg x y
  | Ne -> fun x y -> sign_diff x y
  | Lt -> fun x y -> sign_inf x y
  | Le -> fun x y -> sign_infeg x y
  | Gt -> fun x y -> sign_sup x y
  | Ge -> fun x y -> sign_supeq x y

(* Signs of a condition *)
let sign_cond cond env_sign =
  match cond with
  | expr1, cmp, expr2 ->
      (sign_comp cmp) (sign_expr expr1 env_sign) (sign_expr expr2 env_sign)

let print_sign_list l =
  List.iter (fun x -> print_string (string_of_sign x ^ "")) l

let rec print_signs env =
  match env with
  | (name, sgn) :: env' ->
      print_string (name ^ " ");
      print_sign_list sgn;
      print_string "\n";
      print_signs env'
  | [] -> ()

let get_signs_for_var sb v =
  let rec get_signs_for_var_aux sb v acc =
    match sb with
    | (n, sgns) :: sb' ->
        get_signs_for_var_aux sb' v (if v = n then merge sgns acc else acc)
    | [] -> acc
  in
  get_signs_for_var_aux sb v []

let rec merge_signs sb1 sb2 acc =
  match sb2 with
  | (n, sgns) :: sb2' ->
      merge_signs sb1 sb2'
        (merge acc [ (n, merge sgns (get_signs_for_var sb1 n)) ])
  | [] -> acc

(* Signs of instructions and blocks*)
let rec sign_instr instr env_sign =
  let rec add_signs sb env_sign =
    match sb with
    | [] -> env_sign
    | (n, sgns) :: sb' -> add_signs sb' (add_to_env n sgns env_sign)
  in
  match instr with
  | Set (s, expr) ->
      let x = sign_expr expr env_sign in
      add_to_env s x env_sign
      (*le signe de l valeur de name *)
  | Read name ->
      let s = [ Pos; Neg; Zero ] in
      add_to_env name s env_sign
  | Print expr -> env_sign
  (*
     IF -> (sign_block b1 (sign_cond cond env_sign))
     ELSE -> (sign_block b2 (rev_sign sign_cond))
  *)
  | If (cond, b1, b2) ->
      let signb1 = sign_block b1 env_sign in
      let signb2 = sign_block b2 env_sign in
      let m = merge_signs signb1 signb2 [] in
      add_signs m env_sign
  | While (cond, b1) -> add_signs (sign_block b1 env_sign) env_sign

and sign_block block env_sign =
  match block with
  | (i, instr) :: block' ->
      let env' = sign_instr instr env_sign in
      sign_block block' env'
  | [] -> env_sign

let sign_polish (p : program) : unit =
  let env = sign_block p [] in
  print_signs env;
