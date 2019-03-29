<?php


namespace App;


use Illuminate\Database\Eloquent\Model;

class Note extends Model
{
    protected $table = 'bcard_notes';

    protected $fillable = [
        'business_card_id', 'note'
    ];

    public function business_card() {
        return $this->belongsTo(BusinessCard::class);
    }

}